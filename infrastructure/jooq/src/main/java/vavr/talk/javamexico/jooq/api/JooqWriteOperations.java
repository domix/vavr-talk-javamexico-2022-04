package vavr.talk.javamexico.jooq.api;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Either;
import org.jooq.DSLContext;
import org.jooq.DeleteReturningStep;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.UpdateReturningStep;
import vavr.talk.javamexico.Failure;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public interface JooqWriteOperations extends JooqContext {

    /**
     * Executes the given query returning the element created
     *
     * @param newRecord        any object to be saved
     * @param tableType        any jOOQ's {@link Table} type
     * @param validationGroups the given validations groups if needed
     * @param <T>              any type
     * @param <E>              any jOOQ's table type
     * @param <R>              itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <E extends Table<R>, R extends Record, T> Either<Failure, T> save(T newRecord,
                                                                      E tableType,
                                                                      Class<?>... validationGroups);

    /**
     * Executes the given query returning the element created
     *
     * @param newRecord    any object to be saved
     * @param tableType    any jOOQ's {@link Table} type
     * @param recordMapper {@link RecordMapper} specific type converter
     * @param <T>          any type
     * @param <E>          any jOOQ's table type
     * @param <R>          itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <E extends Table<R>, R extends Record, T> Either<Failure, T> save(T newRecord,
                                                                      E tableType,
                                                                      RecordMapper<R, T> recordMapper,
                                                                      Class<?>... validationGroups);

    /**
     * Executes the given query returning the element created
     *
     * @param record       any jOOQ's {@link Record} type
     * @param recordMapper {@link RecordMapper} specific type converters
     * @param <T>          any type
     * @param <R>          itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <T, R extends Record> Either<Failure, T> save(R record, RecordMapper<R, T> recordMapper);

    /**
     * Executes the given query returning the element created
     *
     * @param newRecord any object to be saved
     * @param tableType any jOOQ's {@link Table} type
     * @param <T>       any type
     * @param <E>       any jOOQ's table type
     * @param <R>       itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <E extends Table<R>, T, R extends Record> Either<Failure, T> update(T newRecord,
                                                                        E tableType,
                                                                        Class<?>... validationGroups);

    /**
     * Executes the given query returning the element created
     *
     * @param updateStatement update statement to execute
     * @param returnTypeClass any type that will hold the returned information after the execution of a query
     * @param <T>             any type
     * @param <R>             itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <T, R extends Record> Either<Failure, T> update(Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                    Class<T> returnTypeClass);

    /**
     * Executes the given query returning the element created
     *
     * @param updateStatement update statement to execute
     * @param recordMapper    {@link RecordMapper} specific type converter
     * @param <T>             any type
     * @param <R>             itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <R extends Record, T> Either<Failure, T> updateAndMap(Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                          RecordMapper<R, T> recordMapper);

    /**
     * Executes the given query, returning a List containing all the updated records.
     *
     * @param updateStatement update statement to execute
     * @param returnTypeClass any type that will hold the returned information after the execution of a query
     * @param <T>             any type
     * @param <R>             itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <T, R extends Record> Either<Failure, List<T>> updateMany(Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                              Class<T> returnTypeClass);

    /**
     * Executes the given query, returning a List containing all the updated records.
     *
     * @param updateStatement update statement to execute
     * @param recordMapper    {@link RecordMapper} specific type converter
     * @param <T>             any type
     * @param <R>             itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <R extends Record, T> Either<Failure, List<T>> updateAndMapMany(Function<DSLContext, UpdateReturningStep<R>> updateStatement,
                                                                    RecordMapper<R, T> recordMapper);

    /**
     * Executes the given delete query returning the element deleted.
     *
     * @param deleteStatement delete statement to execute
     * @param returnTypeClass the type of the returned element
     * @param <T>             any type
     * @param <R>             itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <R extends Record, T> Either<Failure, T> delete(Function<DSLContext, DeleteReturningStep<R>> deleteStatement,
                                                    Class<T> returnTypeClass);

    /**
     * Executes the given delete query returning the element deleted.
     *
     * @param deleteStatement delete statement to execute
     * @param recordMapper    {@link RecordMapper} specific type converter
     * @param <T>             any type
     * @param <R>             itself or any extension of {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <R extends Record, T> Either<Failure, T> delete(Function<DSLContext, DeleteReturningStep<R>> deleteStatement,
                                                    RecordMapper<R, T> recordMapper);

    /**
     * Execute a database operation (possibly an insert, delete or update) in a transactional context.
     *
     * @param operation the operation to execute.
     * @param <T>       the return type of the operation.
     * @return {@link Either} a {@link Failure} (if the operation throws an unexpected exception
     * or returns a {@link Either.Left} or the result of the execution of the operation.
     */
    <T> Either<Failure, T> executeInTransaction(Supplier<Either<Failure, T>> operation);

    /**
     * Executes the given query in sequence all in the same transaction.
     *
     * @param input        the first input element in the chain of executions.
     * @param operationOne the first operation will need executed in a transaction.
     * @param operationTwo the second operation will need executed in a transaction.
     * @param <T>          The type for the input in the operationOne.
     * @param <R>          The type for the input of the operationTwo.
     * @param <S>          The resulted type of the operationTwo (wrapped in an Either with a Failure).
     * @return {@link Either} a {@link Failure} or the result of the all operations in a tuple (R, and S).
     */
    <T, R, S> Either<Failure, Tuple2<R, S>> executeInTransaction(
        T input,
        Function<T, Either<Failure, R>> operationOne,
        Function<R, Either<Failure, S>> operationTwo
    );

    /**
     * Executes the given query in sequence all in the same transaction.
     *
     * @param input          the first input element in the chain of executions.
     * @param operationOne   the first operation will need executed in a transaction.
     * @param operationTwo   the second operation will need executed in a transaction.
     * @param operationThree the third operation will need executed in a transaction.
     * @param <T>            The type for the input in the operationOne.
     * @param <R>            The type for the input of the operationTwo.
     * @param <S>            The type for the input of the operationThree.
     * @param <U>            The resulted type of the operationThree (wrapped in an Either with a Failure).
     * @return {@link Either} a {@link Failure} or the result of the all operations in a tuple (R, S and U).
     */
    <T, R, S, U> Either<Failure, Tuple3<R, S, U>> executeInTransaction(
        T input,
        Function<T, Either<Failure, R>> operationOne,
        Function<R, Either<Failure, S>> operationTwo,
        Function<S, Either<Failure, U>> operationThree
    );

}
