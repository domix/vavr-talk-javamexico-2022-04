package vavr.talk.javamexico.jooq.stream;

import io.vavr.collection.Stream;
import io.vavr.control.Try;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Select;
import vavr.talk.javamexico.jooq.api.JooqContext;
import vavr.talk.javamexico.jooq.factory.TransactionAwareJooqContextFactory;

import javax.sql.DataSource;
import java.util.function.Function;

public final class JooqStreaming implements JooqContext {

    private final DSLContext dslContext;
    private final String domainName;

    private JooqStreaming(final DSLContext dslContext, final String domainName) {
        this.dslContext = dslContext;
        this.domainName = domainName;
    }

    @Override
    public DSLContext getContext() {
        return dslContext;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    public static JooqStreaming create(final DataSource dataSource, final String domainName) {
        final var dslContext = TransactionAwareJooqContextFactory.createContext(dataSource);
        return new JooqStreaming(dslContext, DEFAULT_DOMAIN_NAME);
    }

    public <T, R extends Record> Stream<T> streamAll(final Condition condition,
                                                     final RecordMapper<R, T> recordMapper) {
        return Try.of(() -> query.apply(dslContext))
            .mapTry()
    }

}
