package vavr.talk.javamexico.persistence.test

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import vavr.talk.javamexico.investing.InvestingTerm
import vavr.talk.javamexico.persistence.mapper.InvestingTermDbRepository

@MicronautTest
class InvestingTermDbRepositorySpecs extends DbRepositorySpecification {

    @Inject
    InvestingTermDbRepository investingTermDbRepository

    def 'Test save investing term for case #testCase'() {
        when:
            def result = investingTermDbRepository.save(term)

        then:
            if (testCase == 'invalid_term') {
                assert result.isLeft()
                    && result.left.code == 'investing_term.failure'
            } else {
                assert !result.filter {
                    it.id
                        && it.calculationPeriod == term.calculationPeriod
                }.empty
            }

        where:
            testCase       | term
            'invalid_term' | InvestingTerm.builder().build()
            'success'      | InvestingTerm.builder().calculationPeriod(InvestingTerm.TermPeriod.WEEKLY).build()

    }

}
