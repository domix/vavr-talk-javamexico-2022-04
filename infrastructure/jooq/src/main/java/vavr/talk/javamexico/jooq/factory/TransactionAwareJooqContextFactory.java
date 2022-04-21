package vavr.talk.javamexico.jooq.factory;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultTransactionProvider;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * Simple factory class to wrap and create instances of {@link DSLContext}
 */
public class TransactionAwareJooqContextFactory {

    /**
     * Simple provider of instances of {@link DSLContext} with awareness of transactional scopes
     *
     * @param dataSource {@link DataSource}
     * @return {@link DSLContext}
     */
    public static DSLContext createContext(final @Nonnull DataSource dataSource) {
        final var proxy = new TransactionAwareDataSourceProxy(dataSource);
        final var connectionProvider = new DataSourceConnectionProvider(proxy);
        final var configuration = new DefaultConfiguration();
        final var transactionProvider = new DefaultTransactionProvider(connectionProvider);
        configuration.setConnectionProvider(connectionProvider);
        configuration.setTransactionProvider(transactionProvider);
        configuration.set(SQLDialect.POSTGRES);

        return new DefaultDSLContext(configuration);
    }


}
