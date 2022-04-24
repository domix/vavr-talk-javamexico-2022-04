package vavr.talk.javamexico.jooq.transactional;

import io.vavr.Function3;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.entity.Page;
import vavr.talk.javamexico.jooq.entity.Pageable;
import vavr.talk.javamexico.jooq.entity.Slice;
import vavr.talk.javamexico.jooq.factory.JooqOperationFailures;
import vavr.talk.javamexico.jooq.factory.TransactionAwareJooqContextFactory;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Class with common read-only operations executed with in a transaction-aware {@link DSLContext}
 */
@Slf4j
public class TransactionAwareJooqReadOperations implements JooqReadOperations {

    private final static String FETCH_ONE = "fetch-one";
    private final static String FETCH_MANY = "fetch-many";
    private final static String FETCH_PAGED = "fetch-paged";
    private final static String COUNT = "count";

    private final JooqOperationFailures jooqOperationFailures;
    private final DSLContext dslContext;

    @Getter
    private final String domainName;

    protected TransactionAwareJooqReadOperations(final @Nonnull DSLContext dslContext,
                                                 final @Nonnull String domainName) {
        this.dslContext = dslContext;
        this.domainName = domainName;
        this.jooqOperationFailures = JooqOperationFailures.create(domainName);
    }

    public static TransactionAwareJooqReadOperations create(final @Nonnull DataSource dataSource) {
        final var dslContext = TransactionAwareJooqContextFactory.createContext(dataSource);
        return new TransactionAwareJooqReadOperations(dslContext, DEFAULT_DOMAIN_NAME);
    }

    public static TransactionAwareJooqReadOperations create(final @Nonnull DataSource dataSource,
                                                            final @Nonnull String domainName) {
        final var dslContext = TransactionAwareJooqContextFactory.createContext(dataSource);
        return new TransactionAwareJooqReadOperations(dslContext, domainName);
    }

    @Nonnull
    @Override
    public DSLContext getContext() {
        return this.dslContext;
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, T> get(final @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
                                                        final @Nonnull Class<T> returnTypeClass) {
        return fetchOne(selectQueryAction, result -> result.into(returnTypeClass));
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, T> get(final @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
                                                        final @Nonnull RecordMapper<R, T> recordMapper) {
        return fetchOne(selectQueryAction, recordMapper::map);
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, List<T>> findAll(final @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
                                                                  final @Nonnull Class<T> returnTypeClass) {
        return fetchMany(selectQueryAction, result -> result.into(returnTypeClass));
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, List<T>> findAll(final @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
                                                                  final @Nonnull RecordMapper<R, T> recordMapper) {
        return fetchMany(selectQueryAction, result -> result.map(recordMapper));
    }

    @Nonnull
    @Override
    public <T, R extends Record, E extends Table<R>> Either<Failure, Slice<T>> findAll(
        @Nonnull final E tableType,
        @Nonnull final RecordMapper<R, T> recordMapper
    ) {
        return Try.of(() -> dslContext.selectFrom(tableType).fetch())
            .mapTry(result -> result.map(recordMapper))
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, FETCH_MANY))
            .map(Slice::ofContent);
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, Page<T>> findAllPaged(final @Nonnull Function<DSLContext, SelectLimitStep<R>> selectQueryAction,
                                                                       final @Nonnull Pageable<T> pageable) {
        return Option.of(selectQueryAction.apply(getContext()))
            .toEither(jooqOperationFailures.createFailure(FETCH_PAGED, "invalid-query", "The select query cannot be 'null'"))
            .flatMap(selectQuery -> {
                final var countQuery = getContext().selectCount()
                    .from(selectQuery.asTable()).getQuery();
                return findAllPaged(countQuery, selectQuery, pageable);
            });
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, Page<T>> findAllPaged(final @Nonnull Table<R> tableType,
                                                                       final @Nonnull Condition selectCriteria,
                                                                       final @Nonnull Pageable<T> pageable) {
        final var countQuery = getContext().selectCount().from(tableType).where(selectCriteria).getQuery();
        final var sliceQuery = getContext().selectFrom(tableType).where(selectCriteria);
        return findAllPaged(countQuery, sliceQuery, pageable);
    }

    @Nonnull
    @Override
    public <T, R extends Record> Either<Failure, Page<T>> findAllPaged(final @Nonnull Table<R> tableType,
                                                                       final @Nonnull Pageable<T> pageable) {
        return Option.of(tableType)
            .toEither(jooqOperationFailures.createFailure(FETCH_PAGED, "invalid-query", "The table type cannot be 'null'"))
            .flatMap(validTableType -> {
                final var countQuery = getContext().selectCount().from(validTableType).getQuery();
                final var sliceQuery = getContext().selectFrom(validTableType);
                return findAllPaged(countQuery, sliceQuery, pageable);
            });
    }

    @Override
    public <T, R extends Record> Either<Failure, Page<T>> findAllPaged(final @Nonnull Function<DSLContext, SelectLimitStep<R>> selectQueryAction,
                                                                       final @Nonnull Pageable<T> pageable,
                                                                       final @Nonnull RecordMapper<R, T> recordMapper) {
        return Option.of(selectQueryAction.apply(getContext()))
            .toEither(jooqOperationFailures.createFailure(FETCH_PAGED, "invalid-query", "The select query cannot be 'null'"))
            .flatMap(selectQuery -> {
                final var countQuery = getContext().selectCount()
                    .from(selectQuery.asTable()).getQuery();
                return findAllPaged(countQuery, selectQuery, pageable, recordMapper);
            });
    }

    @Nonnull
    @Override
    public <R extends Record> Either<Failure, Page<R>> findAllPaged(
        final @Nonnull Function<DSLContext, SelectLimitStep<R>> selectQueryAction,
        final int pageNumber,
        final int pageSize
    ) {
        return Option.of(selectQueryAction.apply(getContext()))
            .toEither(jooqOperationFailures.createFailure(FETCH_PAGED,
                "invalid-query", "The select query cannot be 'null'"))
            .flatMap(selectQuery -> {
                final var countQuery = getContext().selectCount().getQuery();
                return findAllPaged(countQuery, selectQuery, pageNumber, pageSize);
            });

    }

    private <T, R extends Record> Either<Failure, Page<T>> findAllPaged(final SelectQuery<Record1<Integer>> countQuery,
                                                                        final SelectLimitStep<R> contentPartialQuery,
                                                                        final Pageable<T> pageable) {
        return findAllPaged(countQuery, contentPartialQuery, pageable,
            record -> record.into(pageable.getResultClass()));
    }

    private <T, R extends Record> Either<Failure, Page<T>> findAllPaged(
        final SelectQuery<Record1<Integer>> countQuery,
        final SelectLimitStep<R> contentPartialQuery,
        final Pageable<T> pageable,
        final RecordMapper<R, T> recordMapper
    ) {
        final Function3<Integer, List<T>, Pageable<T>, Page<T>> basePageBuildCurried = Function3.of(this::buildPage);
        return Try.of(() -> basePageBuildCurried.apply(countQuery.fetchOneInto(Integer.class)))
            .mapTry(totalElementsCurried -> {
                final var content = contentPartialQuery
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch(recordMapper);
                return totalElementsCurried.apply(content);
            })
            .mapTry(pageBuilderCurried -> pageBuilderCurried.apply(pageable))
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, FETCH_PAGED));
    }


    private <R extends Record> Either<Failure, Page<R>> findAllPaged(final SelectQuery<Record1<Integer>> countQuery,
                                                                     final SelectLimitStep<R> contentPartialQuery,
                                                                     final int pageNumber,
                                                                     final int pageSize) {
        return Try.of(() -> {
                final Integer totalElements = countQuery.fetchOneInto(Integer.class);
                final var content = contentPartialQuery
                    .limit(pageSize)
                    .offset(pageSize * (pageNumber - 1))
                    .fetch();
                return this.buildPage(totalElements, content, pageSize);
            })
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, FETCH_PAGED));
    }

    private <T, R extends Record> Either<Failure, T> fetchOne(
        final @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
        final @Nonnull Function<R, T> resultMapper
    ) {
        return Try.of(() -> Objects.requireNonNull(selectQueryAction.apply(dslContext)))
            .mapTry(resultQuery -> resultQuery.fetchOptional(resultMapper::apply))
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, FETCH_ONE))
            .flatMap(handleOptionalResult());

    }

    private <T, R extends Record> Either<Failure, List<T>> fetchMany(final @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
                                                                     final @Nonnull Function<Result<R>, List<T>> resultMapper) {
        return Try.of(() -> Objects.requireNonNull(selectQueryAction.apply(dslContext)))
            .mapTry(ResultQuery::fetch)
            .filterTry(Objects::nonNull)
            .mapTry(resultMapper::apply)
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, FETCH_MANY));
    }

    private <T> Page<T> buildPage(final Integer totalElements,
                                  final List<T> sliceContent,
                                  final Pageable<T> pageable) {
        final double pages = Math.ceil(totalElements.doubleValue() / (double) pageable.getPageSize());
        final var slice = Slice.ofContent(sliceContent);
        return Page.ofSlice(slice)
            .totalElements(totalElements)
            .totalPages((int) pages)
            .build();
    }

    private <T> Page<T> buildPage(final Integer totalElements,
                                  final List<T> sliceContent,
                                  final int pageSize) {
        final double pages = Math.ceil(totalElements.doubleValue() / (double) pageSize);
        final var slice = Slice.ofContent(sliceContent);
        return Page.ofSlice(slice)
            .totalElements(totalElements)
            .totalPages((int) pages)
            .build();
    }

    /**
     * Handles any `Optional<T>` and converts it to a `Either<Failure, T>`
     *
     * @param <T> any type
     * @return {@link Function}
     */
    public <T> Function<Optional<T>, Either<Failure, T>> handleOptionalResult() {
        return optionalResult -> optionalResult
            .map(Either::<Failure, T>right)
            .orElseGet(() -> {
                final var failure = jooqOperationFailures.createFailure(FETCH_ONE,
                    "empty-result", "Result returned is empty or 'null'");
                return Either.left(failure);
            });
    }

    @Override
    public Either<Failure, Integer> count(@Nonnull Function<DSLContext, Integer> selectCountAction) {
        return Try.of(() -> selectCountAction.apply(dslContext))
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, COUNT));
    }

}

