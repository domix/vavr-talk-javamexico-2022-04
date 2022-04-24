package vavr.talk.javamexico.jooq.api;

import io.vavr.control.Either;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import vavr.talk.javamexico.Failure;

import java.util.stream.Stream;

public interface JooqStreamOperations extends JooqContext {

    <E, R extends Record, T extends Table<R>> Either<Failure, Stream<E>> streamAllBy(
        T tableType,
        Condition condition,
        RecordMapper<R, E> recordMapper
    );

    <E, R extends Record, T extends Table<R>> Either<Failure, Stream<E>> streamAll(
        T tableType,
        RecordMapper<R, E> recordMapper
    );

}
