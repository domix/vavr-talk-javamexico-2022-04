package vavr.talk.javamexico.jooq.transactional


import org.jooq.*
import org.jooq.exception.DataAccessException
import org.jooq.exception.MappingException
import org.jooq.exception.NoDataFoundException
import spock.lang.Specification
import vavr.talk.javamexico.jooq.api.JooqReadOperations
import vavr.talk.javamexico.jooq.test.MockJooqContextFactory

import java.sql.SQLException
import java.util.function.Function

import static vavr.talk.javamexico.jooq.test.TestDataProvider.*

class JooqReadOperationsSpecs extends Specification {

    JooqReadOperations jooqReadOperations

    static MockJooqContextFactory<Foo, JooqReadOperations, FooTable> mockContextFactory

    def setupSpec() {
        mockContextFactory = MockJooqContextFactory.builder()
            .tableRecordType(FooTable.FOO_TABLE)
            .recordConverter({ Foo value -> fooToRecord(value) })
            .instanceCreator(TransactionAwareJooqReadOperations::new)
            .domainName('foo-domain')
            .build()
    }

    def 'Test get with return type or record mapper'() {
        setup:
            def foo = new Foo(fooId: 1, fooName: 'A simple foo value')
            if (withException) {
                jooqReadOperations = mockContextFactory.create(new SQLException(expectedException as Throwable))
            } else {
                jooqReadOperations = mockContextFactory.create(foo)
            }

        when:
            def result = jooqReadOperations.get({ DSLContext context ->
                context.selectFrom(FooTable.FOO_TABLE)
                    .where(FooTable.FOO_TABLE.FOO_ID.eq(1)) as Select<Record>
            }, Foo)
        then:
            if (withException) {
                assert result.isLeft()
                assert result.getLeft().code == 'foo-domain.failure.fetch-one'
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }
                assert result.getLeft().reason.contains((expectedException as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get().fooId == foo.fooId
                assert result.get().fooName == foo.fooName
            }

        when:
            result = jooqReadOperations.get({ DSLContext context ->
                context.selectFrom(FooTable.FOO_TABLE)
                    .where(FooTable.FOO_TABLE.FOO_ID.eq(1)) as Select<Record>
            }, RECORD_MAPPER)
        then:
            if (withException) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-one')
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }
                assert result.getLeft().reason.contains((expectedException as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get().fooId == foo.fooId
                assert result.get().fooName == foo.fooName
            }

        where:
            withException || expectedException
            false         || _
            true          || new DataAccessException('Data access exception!')
            true          || new MappingException('Mapping data exception!')
            true          || new NoDataFoundException('No data found exception!')
    }

    def 'Test find all with return type or record mapper'() {
        setup:
            def foos = (1..5).collect {
                new Foo(fooId: it, fooName: "$it: simple foo value")
            }
            if (withException) {
                jooqReadOperations = mockContextFactory.create(new SQLException(expectedException as Throwable))
            } else {
                jooqReadOperations = mockContextFactory.create(foos)
            }

        when:
            def result = jooqReadOperations.findAll({ DSLContext context ->
                context.selectFrom(FooTable.FOO_TABLE)
                    .where(FooTable.FOO_TABLE.FOO_NAME.like('simple foo value')) as Select<Record>
            }, Foo)
        then:
            if (withException) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-many')
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }
                assert result.getLeft().reason.contains((expectedException as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get().every {
                    (it.fooId >= 1 || it.fooId <= 5) &&
                        it.fooName == "${ it.fooId }: simple foo value"
                }
            }

        when:
            result = jooqReadOperations.findAll({ DSLContext context ->
                context.selectFrom(FooTable.FOO_TABLE)
                    .where(FooTable.FOO_TABLE.FOO_NAME.like('simple foo value')) as Select<Record>
            }, RECORD_MAPPER)
        then:
            if (withException) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-many')
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }
                assert result.getLeft().reason.contains((expectedException as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get().every {
                    (it.fooId >= 1 || it.fooId <= 5) &&
                        it.fooName == "${ it.fooId }: simple foo value"
                }
            }

        where:
            withException || expectedException
            false         || _
            true          || new DataAccessException('Data access exception!')
            true          || new MappingException('Mapping data exception!')
    }

    def 'Test find paginated results'() {
        setup:
            def pageable = Pageable.builder()
                .pageSize(2)
                .pageNumber(1)
                .resultClass(Foo.class)
                .build()
            def foos = (1..10).collect {
                new Foo(fooId: it, fooName: "$it: simple foo value")
            }
            if (withException) {
                jooqReadOperations = mockContextFactory.create(new SQLException(expectedException as Throwable))
            } else {
                jooqReadOperations = mockContextFactory.create(createDataProvider(foos, pageable.pageSize))
            }

        when:
            Function<DSLContext, SelectLimitStep<Record>> query = context -> withNullQuery ? null : context.selectFrom(FooTable.FOO_TABLE)
            def result = jooqReadOperations.findAllPaged(query, pageable)
        then:
            if (withException || withNullQuery) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-paged')

                if (withNullQuery) {
                    assert result.getLeft().reason == 'The select query cannot be \'null\''
                    assert result.getLeft().cause.empty
                } else {
                    assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }.present
                    assert result.getLeft().reason.contains((expectedException as Throwable).message)
                }

            } else {
                assert result.isRight()
                assert result.get().totalElements == foos.size()
                assert result.get().totalPages == (foos.size() / pageable.pageSize).intValue()
                assert result.get().elements.numberOfElements == pageable.pageSize
                assert result.get().elements.content.every { foo ->
                    (foo.fooId >= 1 || foo.fooId <= 10) &&
                        foo.fooName == "${ foo.fooId }: simple foo value"
                }
            }
        where:
            withException | withNullQuery || expectedException
            false         | false         || _
            false         | true          || _
            true          | false         || new DataAccessException('Data access exception!')
            true          | false         || new MappingException('Mapping data exception!')
    }

    def 'Test find paginated results with explicit record mapper'() {
        setup:
            def pageable = Pageable.builder()
                .pageSize(2)
                .pageNumber(1)
                .resultClass(Foo.class)
                .build()
            def foos = (1..10).collect {
                new Foo(fooId: it, fooName: "$it: simple foo value")
            }
            if (withException) {
                jooqReadOperations = mockContextFactory.create(new SQLException(expectedException as Throwable))
            } else {
                jooqReadOperations = mockContextFactory.create(createDataProvider(foos, pageable.pageSize))
            }

        when:
            Function<DSLContext, SelectLimitStep<Record>> query = context -> withNullQuery ? null : context.selectFrom(FooTable.FOO_TABLE)
            def result = jooqReadOperations.findAllPaged(query, pageable, RECORD_MAPPER)
        then:
            if (withException || withNullQuery) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-paged')

                if (withNullQuery) {
                    assert result.getLeft().reason == 'The select query cannot be \'null\''
                    assert result.getLeft().cause.empty
                } else {
                    assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }.present
                    assert result.getLeft().reason.contains((expectedException as Throwable).message)
                }

            } else {
                assert result.isRight()
                assert result.get().totalElements == foos.size()
                assert result.get().totalPages == (foos.size() / pageable.pageSize).intValue()
                assert result.get().elements.numberOfElements == pageable.pageSize
                assert result.get().elements.content.every { foo ->
                    (foo.fooId >= 1 || foo.fooId <= 10) &&
                        foo.fooName == "${ foo.fooId }: simple foo value"
                }
            }
        where:
            withException | withNullQuery || expectedException
            false         | false         || _
            false         | true          || _
            true          | false         || new DataAccessException('Data access exception!')
            true          | false         || new MappingException('Mapping data exception!')
    }

    def 'Test find paginated results with table type'() {
        setup:
            def pageable = Pageable.builder()
                .pageSize(2)
                .pageNumber(1)
                .resultClass(Foo.class)
                .build()
            def foos = (1..10).collect {
                new Foo(fooId: it, fooName: "$it: simple foo value")
            }
            if (withException) {
                jooqReadOperations = mockContextFactory.create(new SQLException(expectedException as Throwable))
            } else {
                jooqReadOperations = mockContextFactory.create(createDataProvider(foos, pageable.pageSize))
            }

        when:
            Condition conditions = FooTable.FOO_TABLE.FOO_NAME.like('simple foo value')
            def result = jooqReadOperations.findAllPaged(FooTable.FOO_TABLE, conditions, pageable)
        then:
            if (withException) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-paged')
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }.present
                assert result.getLeft().reason.contains((expectedException as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get().totalElements == foos.size()
                assert result.get().totalPages == (foos.size() / pageable.pageSize).intValue()
                assert result.get().elements.numberOfElements == pageable.pageSize
                assert result.get().elements.content.every { foo ->
                    (foo.fooId >= 1 || foo.fooId <= 10) &&
                        foo.fooName == "${ foo.fooId }: simple foo value"
                }
            }
        where:
            withException || expectedException
            false         || _
            true          || new DataAccessException('Data access exception!')
            true          || new MappingException('Mapping data exception!')
    }

    def 'Test find paginated results with table type but not conditions'() {
        setup:
            def pageable = Pageable.builder()
                .pageSize(2)
                .pageNumber(1)
                .resultClass(Foo.class)
                .build()
            def foos = (1..10).collect {
                new Foo(fooId: it, fooName: "$it: simple foo value")
            }
            if (withException) {
                jooqReadOperations = mockContextFactory.create(new SQLException(expectedException as Throwable))
            } else {
                jooqReadOperations = mockContextFactory.create(createDataProvider(foos, pageable.pageSize))
            }

        when:
            def tableType = (withNullTableType ? null : FooTable.FOO_TABLE)
            def result = jooqReadOperations.findAllPaged(tableType as Table<Record>, pageable)

        then:
            if (withException || withNullTableType) {
                assert result.isLeft()
                assert result.getLeft().code.contains('foo-domain.failure.fetch-paged')

                if (withNullTableType) {
                    assert result.getLeft().reason == 'The table type cannot be \'null\''
                    assert result.getLeft().cause.empty
                } else {
                    assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }.present
                    assert result.getLeft().reason.contains((expectedException as Throwable).message)
                }

            } else {
                assert result.isRight()
                assert result.get().totalElements == foos.size()
                assert result.get().totalPages == (foos.size() / pageable.pageSize).intValue()
                assert result.get().elements.numberOfElements == pageable.pageSize
                assert result.get().elements.content.every { foo ->
                    (foo.fooId >= 1 || foo.fooId <= 10) &&
                        foo.fooName == "${ foo.fooId }: simple foo value"
                }
            }
        where:
            withException | withNullTableType || expectedException
            false         | false             || _
            false         | true              || _
            true          | false             || new DataAccessException('Data access exception!')
            true          | false             || new MappingException('Mapping data exception!')
    }

}
