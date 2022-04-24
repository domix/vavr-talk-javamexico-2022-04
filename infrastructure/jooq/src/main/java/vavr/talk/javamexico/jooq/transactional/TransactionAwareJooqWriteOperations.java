package vavr.talk.javamexico.jooq.transactional;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Batch;
import org.jooq.DSLContext;
import org.jooq.DeleteReturningStep;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateReturningStep;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.exception.FailureException;
import vavr.talk.javamexico.jooq.factory.JooqOperationFailures;
import vavr.talk.javamexico.jooq.factory.TransactionAwareJooqContextFactory;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Class with common read-only operations executed with in a transaction-aware {@link DSLContext}
 */
@Slf4j
public class TransactionAwareJooqWriteOperations implements JooqWriteOperations {

    private final static String UPDATE = "update";
    private final static String SAVE = "save";
    private final static String DELETE = "delete";

    private final static String BATCH_UPDATE = "batch_update";

    private final static String BATCH_INSERT = "batch_insert";
    private final static String EXECUTE_TRANSACTION = "execute-transaction";

    private final DSLContext dslContext;
    private final JooqOperationFailures jooqOperationFailures;
    private final BeanValidator<?> beanValidator;

    @Getter
    private final String domainName;

    private static final Predicate<Record> isValidRecord = r -> Objects.nonNull(r)
        && Objects.nonNull(r.fields()) && r.fields().length > 0;

    protected TransactionAwareJooqWriteOperations(final @Nonnull DSLContext dslContext,
                                                  final @Nonnull String domainName,
                                                  final BeanValidator<?> beanValidator) {
        this.dslContext = dslContext;
        this.domainName = domainName;
        this.jooqOperationFailures = JooqOperationFailures.create(domainName);
        this.beanValidator = beanValidator;
    }

    public static TransactionAwareJooqWriteOperations create(final @Nonnull DataSource dataSource,
                                                             final BeanValidator<?> beanValidator) {
        final var dslContext = TransactionAwareJooqContextFactory.createContext(dataSource);
        return new TransactionAwareJooqWriteOperations(dslContext, DEFAULT_DOMAIN_NAME, beanValidator);
    }

    public static TransactionAwareJooqWriteOperations create(final @Nonnull DataSource dataSource,
                                                             final @Nonnull String domainName,
                                                             final BeanValidator<?> beanValidator) {
        final var dslContext = TransactionAwareJooqContextFactory.createContext(dataSource);
        return new TransactionAwareJooqWriteOperations(dslContext, domainName, beanValidator);
    }

    @Override
    public DSLContext getContext() {
        return this.dslContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Table<R>, R extends Record, T> Either<Failure, T> save(final T newRecord,
                                                                             final E tableType,
                                                                             final Class<?>... validationGroups) {
        final Function<R, T> recordMapper = record -> (T) record.into(newRecord.getClass());
        final Function<T, Either<Failure, T>> executor =
            validated -> executeSave(validated, tableType, recordMapper);
        return validateAndExecute(newRecord, executor, validationGroups);
    }

    @Override
    public <E extends Table<R>, R extends Record, T> Either<Failure, T> save(final T newRecord,
                                                                             final E tableType,
                                                                             final RecordMapper<R, T> recordMapper,
                                                                             final Class<?>... validationGroups) {
        final Function<T, Either<Failure, T>> executor = validated -> executeSave(validated, tableType, recordMapper::map);
        return validateAndExecute(newRecord, executor, validationGroups);
    }

    @Override
    public <T, R extends Record> Either<Failure, T> save(final R record, final RecordMapper<R, T> recordMapper) {
        return getTableFromRecord(record, SAVE)
            .toTry()
            .mapTry(recordTable -> dslContext.insertInto(recordTable).set(record))
            .mapTry(insert -> insert.returning().fetchOne())
            .filterTry(Objects::nonNull)
            .mapTry(recordMapper::map)
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, SAVE));

    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Table<R>, T, R extends Record> Either<Failure, T> update(final T newRecord,
                                                                               final E tableType,
                                                                               final Class<?>... validationGroups) {
        final Function<T, Either<Failure, T>> executor =
            validated -> Try.of(() -> dslContext.newRecord(tableType, validated))
                .mapTry(record -> dslContext.update(tableType.asTable()).set(record))
                .mapTry(insert -> insert.returning().fetchOne())
                .filterTry(Objects::nonNull)
                .mapTry(record -> (T) record.into(newRecord.getClass()))
                .toEither()
                .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, UPDATE));
        return validateAndExecute(newRecord, executor, validationGroups);
    }

    @Override
    public <T, R extends Record> Either<Failure, T> update(final Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                           final Class<T> returnTypeClass) {
        return executeUpdate(updateStatement, record -> record.into(returnTypeClass));
    }

    @Override
    public <T, R extends UpdatableRecord<R>> Optional<Failure> batchUpdate(final List<T> records,
                                                                           final Function<T, R> recordMapper) {
      return Option.when(!records.isEmpty(), records.stream()
          .map(recordMapper)
          .toList()
        )
        .fold(() -> {
            final var failure = jooqOperationFailures.createFailure(BATCH_UPDATE, "empty-batch",
              "The batch to update cannot be empty");
            return Optional.of(failure);
          },
          batch -> Try.of(() -> dslContext.batchUpdate(batch))
            .andThenTry(Batch::execute)
            //Honestly we don't care about the whole return statement, either we could validate the array
            .map(__ -> Option.<Failure>none())
            .getOrElseGet(throwable -> Option.of(Failure.of(throwable)))
            .toJavaOptional()
        );
    }

    @Override
    public <T, R extends UpdatableRecord<R>> Optional<Failure> batchInsert(final List<T> records,
                                                                           final Function<T, R> recordMapper) {
      return Option.when(!records.isEmpty(), records.stream()
          .map(recordMapper)
          .toList()
        )
        .fold(() -> {
            final var failure = jooqOperationFailures.createFailure(BATCH_INSERT, "empty-batch",
              "The batch to insert cannot be empty");
            return Optional.of(failure);
          },
          batch -> Try.of(() -> dslContext.batchInsert(batch))
            .andThenTry(Batch::execute)
            //Honestly we don't care about the whole return statement, either we could validate the array
            .map(__ -> Option.<Failure>none())
            .getOrElseGet(throwable -> Option.of(Failure.of(throwable)))
            .toJavaOptional()
        );
    }

    @Override
    public <R extends Record, T> Either<Failure, T> updateAndMap(final Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                                 final RecordMapper<R, T> recordMapper) {
        return executeUpdate(updateStatement, recordMapper::map);
    }

    @Override
    public <T, R extends Record> Either<Failure, List<T>> updateMany(final Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                                     final Class<T> returnTypeClass) {
        return executeUpdateMany(updateStatement, record -> record.into(returnTypeClass));
    }

    @Override
    public <R extends Record, T> Either<Failure, List<T>> updateAndMapMany(final Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                                           final RecordMapper<R, T> recordMapper) {
        return executeUpdateMany(updateStatement, recordMapper::map);
    }

    @Override
    public <R extends Record, T> Either<Failure, T> delete(final Function<DSLContext, DeleteReturningStep<R>> deleteStatement,
                                                           final Class<T> returnTypeClass) {
        return executeDelete(deleteStatement, record -> record.into(returnTypeClass));
    }

    @Override
    public <R extends Record, T> Either<Failure, T> delete(final Function<DSLContext, DeleteReturningStep<R>> deleteStatement,
                                                           final RecordMapper<R, T> recordMapper) {
        return executeDelete(deleteStatement, recordMapper::map);
    }

    private <T> Either<Failure, T> validateAndExecute(final T newRecord,
                                                      final Function<T, Either<Failure, T>> executor,
                                                      final Class<?>... validationGroups) {
        if (Objects.nonNull(beanValidator)) {
            return beanValidator.validateBean(newRecord, validationGroups)
                .flatMap(executor);
        }
        log.debug("No bean validator provided! Please provide a valid implementation of the BeanValidator interface");
        return executor.apply(newRecord);
    }

    private <E extends Table<R>, R extends Record, T> Either<Failure, T> executeSave(final T newRecord,
                                                                                     final E tableType,
                                                                                     final Function<R, T> resultMapper) {
        return Try.of(() -> dslContext.newRecord(tableType, newRecord))
            .mapTry(record -> dslContext.insertInto(tableType.asTable()).set(record))
            .mapTry(insert -> insert.returning().fetchOne())
            .filterTry(Objects::nonNull)
            .mapTry(resultMapper::apply)
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, SAVE));
    }

    private <R extends Record, T> Either<Failure, T> executeUpdate(final Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                                   final Function<R, T> resultMapper) {
        return Try.of(() -> updateStatement.apply(dslContext))
            .mapTry(statement -> statement.returning().fetchOne())
            .filterTry(Objects::nonNull)
            .mapTry(resultMapper::apply)
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, UPDATE));
    }

    private <R extends Record, T> Either<Failure, List<T>> executeUpdateMany(final Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                                             final Function<R, T> resultMapper) {
        return Try.of(() -> updateStatement.apply(dslContext))
            .mapTry(statement -> statement.returning().fetch())
            .filterTry(Objects::nonNull)
            .mapTry(result -> result.stream().map(resultMapper).toList())
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, UPDATE));
    }

    private <R extends Record, T> Either<Failure, T> executeDelete(final Function<DSLContext, DeleteReturningStep<R>> deleteStatement,
                                                                   final Function<R, T> resultMapper) {

        return Try.of(() -> deleteStatement.apply(dslContext))
            .mapTry(statement -> statement.returning().fetchOne())
            .filterTry(Objects::nonNull)
            .mapTry(resultMapper::apply)
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, DELETE));
    }

    /**
     * Gets the {@link Table} that owns the fields and data within the given {@link Record}
     *
     * @param record    any jOOQ's {@link Record} type
     * @param operation a simple identifier of which operations is being performed
     * @return {@link Either} a {@link Failure} or the {@link Table}
     */
    @SuppressWarnings("unchecked")
    private <R extends Record> Either<Failure, Table<R>> getTableFromRecord(final R record, final String operation) {
        return Option.when(isValidRecord.test(record), record.fields())
            .map(fields -> (TableField<R, ?>) fields[0])
            .map(TableField::getTable)
            .toEither(() -> jooqOperationFailures.createFailure(operation, "invalid-record",
                "The record to be created cannot be null or empty!"));
    }

    @Override
    public <T> Either<Failure, T> executeInTransaction(Supplier<Either<Failure, T>> operation) {
        return Try.of(() -> dslContext.transactionResult(configuration -> operation.get().getOrElseThrow(FailureException::new)))
            .toEither()
            .mapLeft(throwable -> {
                if (throwable instanceof FailureException) {
                    return ((FailureException) throwable).getFailure();
                } else {
                    return jooqOperationFailures.createFailure(throwable, EXECUTE_TRANSACTION);
                }
            });
    }

    @Override
    public <T, R, S> Either<Failure, Tuple2<R, S>> executeInTransaction(T input,
                                                                        Function<T, Either<Failure, R>> operation_one,
                                                                        Function<R, Either<Failure, S>> operation_two) {
        return executeInTransaction(() -> operation_one.apply(input).flatMap(firstResult ->
            operation_two.apply(firstResult).map(secondResult ->
                Tuple.of(firstResult, secondResult))));
    }

    @Override
    public <T, R, S, U> Either<Failure, Tuple3<R, S, U>> executeInTransaction(T input,
                                                                              Function<T, Either<Failure, R>> operationOne,
                                                                              Function<R, Either<Failure, S>> operationTwo,
                                                                              Function<S, Either<Failure, U>> operationThree) {
        return executeInTransaction(() -> operationOne.apply(input).flatMap(firstResult ->
            operationTwo.apply(firstResult).flatMap(secondResult ->
                operationThree.apply(secondResult).map(thirdResult ->
                    Tuple.of(firstResult, secondResult, thirdResult)))));
    }

}
