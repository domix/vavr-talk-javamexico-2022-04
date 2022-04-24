package vavr.talk.javamexico.jooq.api;

import io.vavr.control.Either;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Select;
import org.jooq.SelectLimitStep;
import org.jooq.Table;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.jooq.entity.Page;
import vavr.talk.javamexico.jooq.entity.Pageable;
import vavr.talk.javamexico.jooq.entity.Slice;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public interface JooqReadOperations extends JooqContext {

    /**
     * Reads one element from the result set of the given query
     *
     * @param selectQueryAction The query to be executed
     * @param returnTypeClass   any type that will hold the returned information after the execution of a query
     * @param <T>               any type
     * @param <R>               any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    @Nonnull
    <T, R extends Record> Either<Failure, T> get(
        @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
        @Nonnull Class<T> returnTypeClass
    );

    /**
     * Reads one element from the result set of the given query
     *
     * @param selectQueryAction The query to be executed
     * @param recordMapper      {@link org.jooq.RecordMapper} specific type converter
     * @param <T>               any type
     * @param <R>               any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    @Nonnull
    <T, R extends Record> Either<Failure, T> get(
        @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
        @Nonnull RecordMapper<R, T> recordMapper
    );

    /**
     * Reads all the elements from the result set of the given query
     *
     * @param selectQueryAction The query to be executed
     * @param returnTypeClass   any type that will hold the returned information after the execution of a query
     * @param <T>               any type
     * @param <R>               any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    <T, R extends Record> Either<Failure, List<T>> findAll(
        @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
        @Nonnull Class<T> returnTypeClass
    );

    /**
     * Reads all the elements from the result set of the given query
     *
     * @param selectQueryAction The query to be executed
     * @param recordMapper      {@link RecordMapper} specific type converter
     * @param <T>               any type
     * @param <R>               any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    @Nonnull
    <T, R extends Record> Either<Failure, List<T>> findAll(
        @Nonnull Function<DSLContext, Select<R>> selectQueryAction,
        @Nonnull RecordMapper<R, T> recordMapper
    );

    /**
     * Reads a slice of {@link T} from the result set of the given query
     *
     * @param tableType    any {@link Table<R>} type
     * @param recordMapper {@link RecordMapper} specific type converter
     * @param <T>          any type
     * @param <R>          any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or the result of the execution of the query
     */
    @Nonnull
    <T, R extends Record, E extends Table<R>> Either<Failure, Slice<T>> findAll(
        @Nonnull E tableType,
        @Nonnull RecordMapper<R, T> recordMapper
    );

    /**
     * Reads a single page in the result set of the given query
     *
     * @param selectQueryAction The query to be executed
     * @param pageable          {@link Pageable} pagination info
     * @param <T>               any type
     * @param <R>               any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or a {@link Page}
     */
    @Nonnull
    <T, R extends Record> Either<Failure, Page<T>> findAllPaged(
        @Nonnull Function<DSLContext, SelectLimitStep<R>> selectQueryAction,
        @Nonnull Pageable<T> pageable
    );

    /**
     * Reads a single page in the result set of the given query.
     * The result is a Page of Records, usefully when making joins the record hold the values of n tables.
     * No mapper it's applied.
     *
     * @param selectQueryAction The query to be executed
     * @param pageNumber        {@link int}
     * @param pageSize          {@link int}
     * @param <R>               any type that extends from {@link Record}
     * @return {@link Either} a {@link Failure} or a {@link Page}
     */
    @Nonnull
    <R extends Record> Either<Failure, Page<R>> findAllPaged(
        @Nonnull Function<DSLContext, SelectLimitStep<R>> selectQueryAction,
        int pageNumber,
        int pageSize
    );

    /**
     * Reads a single page from the {@link org.jooq.Table} type that matches the given criteria
     *
     * @param tableType      {@link org.jooq.Table<R>} a jOOQ's table type
     * @param selectCriteria {@link org.jooq.Condition} the conditions to be applied to
     * @param pageable       {@link Pageable} pagination info
     * @param <T>            any type
     * @return {@link Either} a {@link Failure} or a {@link Page}
     */
    @Nonnull
    <T, R extends Record> Either<Failure, Page<T>> findAllPaged(@Nonnull Table<R> tableType,
                                                                @Nonnull Condition selectCriteria,
                                                                @Nonnull Pageable<T> pageable);

    /**
     * Reads a single page in the result set of the given {@link Table}
     *
     * @param tableType {@link Table<R>} a jOOQ's table type
     * @param pageable  {@link Pageable} pagination info
     * @param <T>       any type
     * @return {@link Either} a {@link Failure} or a {@link Page}
     */
    <T, R extends Record> Either<Failure, Page<T>> findAllPaged(@Nonnull Table<R> tableType,
                                                                @Nonnull Pageable<T> pageable);

    <T, R extends Record> Either<Failure, Page<T>> findAllPaged(
        @Nonnull Function<DSLContext, SelectLimitStep<R>> selectQueryAction,
        @Nonnull Pageable<T> pageable,
        @Nonnull RecordMapper<R, T> recordMapper
    );

    Either<Failure, Integer> count(@Nonnull Function<DSLContext, Integer> selectCountAction);

}
