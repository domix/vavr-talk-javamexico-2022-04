package vavr.talk.javamexico.business.interest.impl

import io.vavr.control.Either
import spock.lang.Specification
import vavr.talk.javamexico.Failure
import vavr.talk.javamexico.business.interest.InterestCalculationContext
import vavr.talk.javamexico.repository.InvestingAccountRepository
import vavr.talk.javamexico.repository.InvestingUserRepository

class DefaultInterestCalculationSpecs extends Specification {

  def foo() {
    given:
      def accountRepository = Stub(InvestingAccountRepository)
      def userRepository = Stub(InvestingUserRepository)
      //userRepository.find(_ as Long) >> Either.right(InvestingUser.builder().id(1l).build())
      userRepository.find(_ as Long) >> Either.left(Failure.of("", "user not found"))
      def underTest = new DefaultInterestCalculation(userRepository, accountRepository)

      def calculationContext = InterestCalculationContext.builder()
        .contracts([])
        .build()
    when:
      def interestFor = underTest.process(calculationContext, 1l)
    then:
      interestFor.isPresent()
      interestFor.get().reason == 'user not found'
  }
}
