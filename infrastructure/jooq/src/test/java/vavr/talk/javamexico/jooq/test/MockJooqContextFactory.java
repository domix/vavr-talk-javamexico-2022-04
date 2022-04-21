package vavr.talk.javamexico.jooq.test;

import lombok.Builder;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.Mock;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import vavr.talk.javamexico.jooq.api.JooqContext;

import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Factory helper class to create and configure a mocked instance of an extension type of {@link JooqContext}
 *
 * @param <E> any type
 * @param <R> any class that extends or implements the {@link JooqContext}
 * @param <T> any jOOQ's {@link Table} definition
 */
@Builder
public final class MockJooqContextFactory<E, R extends JooqContext, T extends Table<Record>> {

    @Builder.Default
    private final SQLDialect sqlDialect = SQLDialect.POSTGRES;

    @Builder.Default
    private final String domainName = JooqContext.DEFAULT_DOMAIN_NAME;

    private final BiFunction<DSLContext, String, R> instanceCreator;
    private final Function<E, ? extends Record> recordConverter;
    private final T tableRecordType;

    public R create(final E mockData) {
        final var record = recordConverter.apply(mockData);
        final var mockDataProvider = Mock.of(record);
        return create(mockDataProvider);
    }

    public R create(final List<E> mockData) {
        final var records = mockData.stream()
            .map(recordConverter)
            .collect(Collectors.toList());
        final var mockDataProvider = Mock.of(createMockResult(records));
        return create(mockDataProvider);
    }

    public R create(final Throwable throwable) {
        final var mockDataProvider = Mock.of(new SQLException(throwable));
        return create(mockDataProvider);
    }

    public R create(final MockDataProvider mockDataProvider) {
        final var mockConnection = new MockConnection(mockDataProvider);
        final var mockDslContext = DSL.using(mockConnection, sqlDialect);
        return instanceCreator.apply(mockDslContext, domainName);
    }

    public R create(final Field<Integer> stubbedCountField,
                    final List<E> results,
                    final int pageSize) {
        final var slice = results.subList(0, pageSize);
        final MockDataProvider mockDataProvider = (MockExecuteContext context) -> {
            if (context.sql().contains("count(*)")) {
                final var record = DSL.using(sqlDialect)
                    .newRecord(stubbedCountField)
                    .values(results.size());
                return new MockResult[]{new MockResult(record)};
            }
            final var mockedResults = slice.stream().map(recordConverter)
                .collect(Collectors.toList());
            return new MockResult[]{createMockResult(mockedResults)};
        };
        return create(mockDataProvider);
    }

    private MockResult createMockResult(final List<? extends Record> mockData) {
        final var result = DSL.using(sqlDialect).newResult(tableRecordType);
        result.addAll(mockData);
        return new MockResult(mockData.size(), result);
    }

    public R createWithResultsInSequence(final List<MockResult> mockResults) {
        final MockDataProvider mockDataProvider = new MockDataProvider() {
            int callNumber = -1;

            @Override
            public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
                callNumber++;
                return new MockResult[]{mockResults.get(min(callNumber, mockResults.size()))};
            }
        };
        return create(mockDataProvider);
    }

    public MockResult toMockResult(final E mockData) {
        return new MockResult(recordConverter.apply(mockData));
    }

    public MockResult toMockResult(SQLException exception) {
        return new MockResult(exception);
    }

}

