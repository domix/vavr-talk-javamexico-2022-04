package vavr.talk.javamexico.business.interest.impl

import io.vavr.control.Either
import spock.lang.Specification
import vavr.talk.javamexico.Failure
import vavr.talk.javamexico.business.interest.InterestCalculationContext
import vavr.talk.javamexico.investing.InvestingUser
import vavr.talk.javamexico.persistence.db.repository.InvestingContractDbRepository
import vavr.talk.javamexico.repository.InvestingAccountRepository
import vavr.talk.javamexico.repository.InvestingContractMovementRepository
import vavr.talk.javamexico.repository.InvestingUserRepository

class DefaultInterestCalculationSpecs extends Specification {

  def 'should fail due user is not found'() {
    given:
      def accountRepository = Stub(InvestingAccountRepository)
      def userRepository = Stub(InvestingUserRepository)
      userRepository.find(_ as Long) >> Either.left(Failure.of("user not found"))
      def movementRepository = Stub(InvestingContractMovementRepository)
      def underTest = new DefaultInterestCalculation(userRepository, accountRepository, movementRepository)

      def calculationContext = InterestCalculationContext.builder()
        .contracts([])
        .build()
    when:
      def interestFor = underTest.process(calculationContext, 1l)
    then:
      interestFor.present
      interestFor.get().reason == 'user not found'
  }

  def 'should fail due accountRepository can not retrieve the users accounts'() {
    given:
      def accountRepository = Stub(InvestingAccountRepository)
      def userRepository = Stub(InvestingUserRepository)
      userRepository.find(_ as Long) >> Either.right(InvestingUser.builder().id(1l).build())
      accountRepository.findAllActiveAccounts(_ as Long) >> Either.left(Failure.of("No databass"))
      def movementRepository = Stub(InvestingContractMovementRepository)
      def underTest = new DefaultInterestCalculation(userRepository, accountRepository, movementRepository)

      def calculationContext = InterestCalculationContext.builder()
        .contracts([])
        .build()
    when:
      def result = underTest.process(calculationContext, 1L)
    then:
      result.present
  }

  def 'should fail due accountRepository can not retrieve the users accounts 2'() {
    given:
      def accountRepository = Stub(InvestingAccountRepository)
      def userRepository = Stub(InvestingUserRepository)
      userRepository.find(_ as Long) >> Either.right(InvestingUser.builder().id(1l).build())
      accountRepository.findAllActiveAccounts(_ as Long) >> Either.right([])
      def movementRepository = Stub(InvestingContractMovementRepository)
      def underTest = new DefaultInterestCalculation(userRepository, accountRepository, movementRepository)

      def calculationContext = InterestCalculationContext.builder()
        .contracts([])
        .build()
    when:
      def result = underTest.process(calculationContext, 1L)
    then:
      result.empty
  }


}
