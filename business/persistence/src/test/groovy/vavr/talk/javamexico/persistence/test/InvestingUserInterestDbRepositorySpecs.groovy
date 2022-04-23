package vavr.talk.javamexico.persistence.test

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import vavr.talk.javamexico.investing.InvestingTerm
import vavr.talk.javamexico.investing.InvestingUserInterest
import vavr.talk.javamexico.persistence.db.repository.InvestingTermDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingUserInterestDbRepository

import java.time.OffsetDateTime

import static vavr.talk.javamexico.investing.InvestingTerm.TermPeriod.WEEKLY

@MicronautTest
class InvestingUserInterestDbRepositorySpecs extends DbRepositorySpecification {

    @Inject
    InvestingUserInterestDbRepository investingUserInterestDbRepository

    @Inject
    InvestingTermDbRepository investingTermDbRepository

    def 'Test save user interest some and then stream them'() {
        given:
            def userId = 1
            def investingTerm = investingTermDbRepository.save(investingTerm()).get()
            def userInterests = [] as List<InvestingUserInterest>
            10.times {
                final def interest = InvestingUserInterest.builder()
                    .investingTermId(investingTerm.id)
                    .userId(userId)
                    .currency('mxn')
                    .startBalance(1000.00)
                    .averageBalance(1075.00)
                    .endBalance(1150.00)
                    .interest(15.00)
                    .accruedInterest(150.00)
                    .created(OffsetDateTime.now())
                    .build()
                userInterests.add(interest)
            }
        expect:
            userInterests.collect {
                investingUserInterestDbRepository.save(it)
            }
                .every {
                    it.isRight() &&
                        it.filter { interest ->
                            interest.userId in userInterests.userId
                                && interest.averageBalance in userInterests.averageBalance
                                && interest.endBalance in userInterests.endBalance
                                && interest.startBalance in userInterests.startBalance
                                && interest.interest in userInterests.interest
                                && interest.accruedInterest in userInterests.accruedInterest
                                && interest.currency in userInterests.currency
                                && interest.investingTermId in userInterests.investingTermId
                        }.isDefined()
                }

        when: "Now let's stream them"
            def result = investingUserInterestDbRepository.streamAllByUserId(userId)
        then:
            result.isRight()
    }

    static InvestingTerm investingTerm() {
        InvestingTerm.builder().calculationPeriod(WEEKLY).created(OffsetDateTime.now()).build()
    }

}
