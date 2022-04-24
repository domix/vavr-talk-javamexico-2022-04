package vavr.talk.javamexico.persistence.db.repository

import spock.lang.Ignore
import vavr.talk.javamexico.investing.InvestingContract
import vavr.talk.javamexico.persistence.db.repository.InvestingContractDbRepository
import vavr.talk.javamexico.persistence.test.DbRepositorySpecification

import static vavr.talk.javamexico.Failure.ErrorType.VALIDATION

@Ignore
class InvestingContractDbRepositorySpecs extends DbRepositorySpecification {

    InvestingContractDbRepository investingContractDbRepository

    def setup() {
        investingContractDbRepository = InvestingContractDbRepository.create(dataSource, beanValidator)
    }

    def 'Test save investing term for case #testCase'() {
        when:
            def investingContract = createContract(testCase)
            def result = investingContractDbRepository.save(investingContract)

        then:
            if (testCase == 'invalid_term') {
                assert result.isLeft() &&
                    result.left.reason == "Class 'InvestingContract' has invalid property values" &&
                    result.left.code == 'failure.validation' &&
                    result.left.details
                        .every { it.type == VALIDATION && it.localizedMessage in expectedDetails }
            } else {
                assert !result.filter {
                    it.id
                        && it.currency == investingContract.currency
                        && it.annualInterestRate == investingContract.annualInterestRate
                        && it.createdAt
                        && it.updatedAt
                }.empty
            }

        where:
            testCase           || expectedDetails      | expectedErrorType
            'invalid_contract' || ['must not be null'] | VALIDATION
            'success'          || []                   | _
    }

    static InvestingContract createContract(final String testCase) {
        if (testCase == 'invalid_contract') {
            return InvestingContract.builder()
                .build()
        }
        InvestingContract.builder()
            .contractName("$testCase-contract")
            .currency('mxn')
            .annualInterestRate('15.00')
            .build()
    }

}
