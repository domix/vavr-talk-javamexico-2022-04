package vavr.talk.javamexico.jooq.api;

import io.vavr.collection.Stream;
import io.vavr.control.Either;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import vavr.talk.javamexico.Failure;

public interface JooqStreamOperations extends JooqContext {

    <E, R extends Record, T extends Table<R>> Either<Failure, Stream<E>> streamAllBy(
        T tableType,
        Condition condition,
        RecordMapper<R, E> recordMapper
    );

}
