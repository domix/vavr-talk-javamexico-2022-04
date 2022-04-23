package vavr.talk.javamexico.persistence.test


import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.jooq.exception.DataAccessException
import vavr.talk.javamexico.investing.InvestingTerm
import vavr.talk.javamexico.persistence.mapper.InvestingTermDbRepository

import java.time.OffsetDateTime

import static vavr.talk.javamexico.Failure.ErrorType.VALIDATION
import static vavr.talk.javamexico.investing.InvestingTerm.TermPeriod.WEEKLY

@MicronautTest
class InvestingTermDbRepositorySpecs extends DbRepositorySpecification {

    @Inject
    InvestingTermDbRepository investingTermDbRepository

    def 'Test save investing term for case #testCase'() {
        when:
            def result = investingTermDbRepository.save(term)

        then:
            if (testCase == 'invalid_term') {
                assert result.isLeft() &&
                    result.left.reason == "Class 'InvestingTerm' has invalid property values" &&
                    result.left.code == 'failure.validation' &&
                    result.left.details
                        .every { it.type == VALIDATION && it.localizedMessage in expectedDetails }
            } else if (testCase == 'save_error') {
                assert result.isLeft() &&
                    result.left.reason == 'failure.i18.default' &&
                    result.left.cause.filter { it instanceof DataAccessException } &&
                    result.left.code == 'investing_term.failure.save'
            } else {
                assert !result.filter {
                    it.id
                        && it.calculationPeriod == term.calculationPeriod
                }.empty
            }

        where:
            testCase       | term                                                                                    || expectedDetails      | expectedErrorType
            'invalid_term' | InvestingTerm.builder().build()                                                         || ['must not be null'] | VALIDATION
            'save_error'   | InvestingTerm.builder().calculationPeriod(WEEKLY).build()                               || ['']                 | _
            'success'      | InvestingTerm.builder().calculationPeriod(WEEKLY).created(OffsetDateTime.now()).build() || []                   | _
    }

}
