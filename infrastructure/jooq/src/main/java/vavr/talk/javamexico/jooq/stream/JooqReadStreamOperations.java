package vavr.talk.javamexico.jooq.stream;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.jooq.api.JooqStreamOperations;
import vavr.talk.javamexico.jooq.factory.JooqOperationFailures;
import vavr.talk.javamexico.jooq.factory.TransactionAwareJooqContextFactory;

import javax.sql.DataSource;
import java.util.stream.Stream;

public final class JooqReadStreamOperations implements JooqStreamOperations {

    static final String STREAM = "stream";
    private final DSLContext dslContext;
    private final String domainName;

    private final JooqOperationFailures jooqOperationFailures;

    private JooqReadStreamOperations(final DSLContext dslContext, final String domainName) {
        this.dslContext = dslContext;
        this.domainName = domainName;
        this.jooqOperationFailures = JooqOperationFailures.create(domainName);
    }

    @Override
    public DSLContext getContext() {
        return dslContext;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    public static JooqReadStreamOperations create(final DataSource dataSource, final String domainName) {
        final var dslContext = TransactionAwareJooqContextFactory.createContext(dataSource);
        return new JooqReadStreamOperations(dslContext, domainName);
    }

    @Override
    public <E, R extends Record, T extends Table<R>> Either<Failure, Stream<E>> streamAllBy(
        final T tableType,
        final Condition condition,
        final RecordMapper<R, E> recordMapper
    ) {
        return Try.of(() -> dslContext.selectFrom(tableType)
                .where(condition)
                .stream()
                .map(recordMapper::map)
            )
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, STREAM));
    }

    @Override
    public <E, R extends Record, T extends Table<R>> Either<Failure, Stream<E>> streamAll(
        final T tableType,
        final RecordMapper<R, E> recordMapper
    ) {
        return Try.of(() -> dslContext.selectFrom(tableType)
                .stream()
                .map(recordMapper::map)
            )
            .toEither()
            .mapLeft(throwable -> jooqOperationFailures.createFailure(throwable, STREAM));
    }

}
