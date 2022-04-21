package vavr.talk.javamexico.jooq.test

import io.vavr.Tuple2
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult

/**
 * Helper class to provide data and types for testing purposes
 */
class TestDataProvider {

    static final RecordMapper<Record, Foo> RECORD_MAPPER = record ->
        new Foo(fooId: record.get(FooTable.FOO_TABLE.FOO_ID), fooName: record.get(FooTable.FOO_TABLE.FOO_NAME))

    /**
     * Dummy implementation of a jOOQ's {@link org.jooq.Table} for testing purposes
     */
    static class FooTable extends TableImpl<Record> {

        static final SQLDialect DEFAULT_SQL_DIALECT = SQLDialect.POSTGRES

        static final FooTable FOO_TABLE = new FooTable()

        final TableField<Record, Integer> FOO_ID = createField(DSL.name('foo_id'),
            SQLDataType.INTEGER.nullable(false).identity(true), this, '')
        final TableField<Record, String> FOO_NAME = createField(DSL.name('foo_name'),
            SQLDataType.VARCHAR(20).nullable(false), this, '')

        FooTable() {
            super(DSL.name('foo'), null, FOO_TABLE, null, DSL.comment(''), TableOptions.table())
        }
    }

    static class BarTable extends TableImpl<Record> {

        static final SQLDialect DEFAULT_SQL_DIALECT = SQLDialect.POSTGRES

        static final BAR_TABLE = new BarTable()

        final TableField<Record, Integer> BAR_ID = createField(DSL.name('bar_id'),
            SQLDataType.INTEGER.nullable(false).identity(true), this, '')
        final TableField<Record, Integer> FOO_ID = createField(DSL.name('foo_id'),
            SQLDataType.INTEGER.nullable(false));
        final TableField<Record, String> BAR_NAME = createField(DSL.name('bar_name'),
            SQLDataType.VARCHAR(20).nullable(false), this, '')

        BarTable() {
            super(DSL.name('bar'), null, BAR_TABLE, null, DSL.comment(''), TableOptions.table())
        }

    }

    /**
     * Dummy Java objects for testing purposes
     */
    static class Foo {
        @Positive(message = 'Non-positive or zero!')
        int fooId
        @NotBlank(message = 'Blank or null not allowed!')
        String fooName
    }

    static class Bar {
        @Positive(message = 'Non-positive or zero!')
        int barId
        @Positive(message = 'Non-positive or zero!')
        int fooId
        @NotBlank(message = 'Blank or null not allowed!')
        String barName
    }

    static Record fooToRecord(final Foo value) {
        def fooRecord = FooTable.FOO_TABLE.newRecord()
        fooRecord.setValue(FooTable.FOO_TABLE.FOO_NAME, value.fooName)
        fooRecord.setValue(FooTable.FOO_TABLE.FOO_ID, value.fooId)

        fooRecord
    }

    static Record barAndJoinToRecord(final Tuple2<Foo, Bar> tuple) {
        def joinRecord = DSL.using(SQLDialect.POSTGRES)
            .newRecord(
                FooTable.FOO_TABLE.FOO_ID,
                FooTable.FOO_TABLE.FOO_NAME,
                BarTable.BAR_TABLE.BAR_ID,
                BarTable.BAR_TABLE.FOO_ID,
                BarTable.BAR_TABLE.BAR_NAME
            )
        joinRecord.setValue(FooTable.FOO_TABLE.FOO_ID, tuple._1.fooId)
        joinRecord.setValue(FooTable.FOO_TABLE.FOO_NAME, tuple._1.fooName)

        joinRecord.setValue(BarTable.BAR_TABLE.BAR_ID, tuple._2.barId)
        joinRecord.setValue(BarTable.BAR_TABLE.BAR_NAME, tuple._2.barName)
        joinRecord.setValue(BarTable.BAR_TABLE.FOO_ID, tuple._2.fooId)

        joinRecord
    }

    static MockResult foosAsResult(final List<Foo> foos) {
        def result = DSL.using(FooTable.DEFAULT_SQL_DIALECT).newResult(FooTable.FOO_TABLE)
        result.addAll(foos.collect { foo -> fooToRecord(foo) })
        new MockResult(foos.size(), result)
    }

    static MockResult barJoinAsResult(final List<Tuple2<Foo, Bar>> tuples) {
        def result = DSL.using(BarTable.DEFAULT_SQL_DIALECT)
            .newResult(
                FooTable.FOO_TABLE.FOO_ID,
                FooTable.FOO_TABLE.FOO_NAME,
                BarTable.BAR_TABLE.BAR_ID,
                BarTable.BAR_TABLE.BAR_NAME,
                BarTable.BAR_TABLE.FOO_ID
            )
        result.addAll(tuples.collect { tuple -> barAndJoinToRecord(tuple) } as Collection<? extends Record5<Integer, String, Integer, String, Integer>>)
        new MockResult(tuples.size(), result)
    }

    static MockDataProvider createDataProvider(final List<Foo> foos, final int pageSize) {
        final var slice = foos.subList(0, pageSize)
        var mockResults = new MockResult[]{foosAsResult(slice)}
        return createDataProvider(
            foos.size(),
            FooTable.FOO_TABLE.FOO_ID,
            mockResults
        )
    }

    static MockDataProvider createFooJoinBarDataProvider(final List<Tuple2<Foo, Bar>> data, final int pageSize) {
        final var slice = data.subList(0, pageSize)
        var mockResults = new MockResult[]{barJoinAsResult(slice)}
        return createDataProvider(
            data.size(),
            BarTable.BAR_TABLE.BAR_ID,
            mockResults
        )
    }

    static MockDataProvider createDataProvider(int total,
                                               TableField<Record, Integer> field,
                                               MockResult[] mockResults) {

        MockDataProvider mockDataProvider = { MockExecuteContext context ->
            if (context.sql().contains('count(*)')) {
                def record = DSL.using(SQLDialect.POSTGRES)
                    .newRecord(field)
                    .values(total)
                return new MockResult[]{new MockResult(record)}
            }
            return mockResults
        }
        return mockDataProvider
    }

}
