package vavr.talk.javamexico.jooq.transactional

import org.jooq.exception.DataAccessException
import org.jooq.exception.MappingException
import org.jooq.tools.jdbc.MockResult
import spock.lang.Specification
import vavr.talk.javamexico.jooq.api.JooqReadOperations
import vavr.talk.javamexico.jooq.api.JooqWriteOperations
import vavr.talk.javamexico.jooq.test.MockJooqContextFactory

import java.sql.SQLException

import static vavr.talk.javamexico.jooq.test.TestDataProvider.*

class JooqWriteTransactionOperationsSpecs extends Specification {

    JooqWriteOperations jooqWriteOperations

    static MockJooqContextFactory<Foo, JooqReadOperations, FooTable> mockContextFactory

    def setupSpec() {
        mockContextFactory = MockJooqContextFactory.builder()
            .tableRecordType(FooTable.FOO_TABLE)
            .recordConverter({ Foo value -> fooToRecord(value) })
            .instanceCreator(TransactionAwareJooqWriteOperations::new)
            .domainName('foo-domain')
            .build()
    }

    def 'A transaction fails and is rolled back when an exception is thrown'() {
        setup:
            jooqWriteOperations = mockContextFactory.create([]) as JooqWriteOperations

        when:
            def exception = new RuntimeException("saraza")
            def result = jooqWriteOperations.executeInTransaction {
                throw exception
            }

        then:
            result.isLeft()
            result.left.cause.get() == exception
    }

    def 'A transaction fails and is rolled back when a left is returned down the line'() {
        given:
            def foo = new Foo(fooId: 1, fooName: 'A simple foo value')
            MockResult goodResult = mockContextFactory.toMockResult(foo)
            MockResult badResult = mockContextFactory.toMockResult(new SQLException(new RuntimeException("saraza")))

            jooqWriteOperations = mockContextFactory.createWithResultsInSequence([goodResult, badResult])
                as JooqWriteOperations

        when:
            def result = jooqWriteOperations.executeInTransaction {
                jooqWriteOperations.save(foo, FooTable.FOO_TABLE).flatMap { result1 ->
                    jooqWriteOperations.save(foo, FooTable.FOO_TABLE)
                }
            }

        then:
            result.isLeft()
    }

    def 'Two savings in a transaction'() {
        setup:
            def foo = new Foo(fooId: 1, fooName: 'A simple foo value')
            MockResult goodResult = mockContextFactory.toMockResult(foo)
            MockResult badResult = mockContextFactory.toMockResult(new SQLException((Throwable) exception))
            jooqWriteOperations = mockContextFactory.createWithResultsInSequence(
                List.of(
                    isExceptionFirst ? badResult : goodResult,
                    isExceptionSecond ? badResult : goodResult
                )
            )
                as JooqWriteOperations
        when:
            def result = jooqWriteOperations.executeInTransaction(foo,
                input1 -> jooqWriteOperations.save(input1, FooTable.FOO_TABLE),
                input2 -> jooqWriteOperations.save(input2, FooTable.FOO_TABLE)
            )
        then:
            if (isExceptionFirst || isExceptionSecond) {
                assert result.isLeft()
                assert result.getLeft().code == 'foo-domain.failure.save'
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }
                assert result.getLeft().reason.contains((exception as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get()._1.fooId == foo.fooId
                assert result.get()._1.fooName == foo.fooName
                assert result.get()._2.fooId == foo.fooId
                assert result.get()._2.fooName == foo.fooName
            }
        where:
            isExceptionFirst || isExceptionSecond || exception
            true             || true              || new DataAccessException('Data access exception!')
            true             || false             || new MappingException('Mapping data exception!')
            false            || true              || new MappingException('Mapping data exception!')
            false            || false             || new MappingException('Mapping data exception!')
    }

    def 'Three savings in a transaction'() {
        setup:
            def foo = new Foo(fooId: 1, fooName: 'A simple foo value')
            MockResult goodResult = mockContextFactory.toMockResult(foo)
            MockResult badResult = mockContextFactory.toMockResult(new SQLException((Throwable) exception))
            jooqWriteOperations = mockContextFactory.createWithResultsInSequence(
                List.of(
                    isExceptionFirst ? badResult : goodResult,
                    isExceptionSecond ? badResult : goodResult,
                    isExceptionThird ? badResult : goodResult
                )
            )
                as JooqWriteOperations
        when:
            def result = jooqWriteOperations.executeInTransaction(foo,
                input1 -> jooqWriteOperations.save(input1, FooTable.FOO_TABLE),
                input2 -> jooqWriteOperations.save(input2, FooTable.FOO_TABLE),
                input3 -> jooqWriteOperations.save(input3, FooTable.FOO_TABLE,
                )
            )
        then:
            if (isExceptionFirst || isExceptionSecond || isExceptionThird) {
                assert result.isLeft()
                assert result.getLeft().code == 'foo-domain.failure.save'
                assert result.getLeft().cause.filter { ex -> ex instanceof DataAccessException }
                assert result.getLeft().reason.contains((exception as Throwable).message)

            } else {
                assert result.isRight()
                assert result.get()._1.fooId == foo.fooId
                assert result.get()._1.fooName == foo.fooName
                assert result.get()._2.fooId == foo.fooId
                assert result.get()._2.fooName == foo.fooName
            }
        where:
            isExceptionFirst || isExceptionSecond || isExceptionThird || exception
            true             || true              || true             || new DataAccessException('Data access exception!')
            true             || true              || false            || new DataAccessException('Data access exception!')
            true             || false             || true             || new DataAccessException('Data access exception!')
            true             || false             || false            || new DataAccessException('Data access exception!')
            false            || true              || true             || new DataAccessException('Data access exception!')
            false            || true              || false            || new DataAccessException('Data access exception!')
            false            || false             || true             || new DataAccessException('Data access exception!')
            false            || false             || false            || new DataAccessException('Data access exception!')
    }

}
