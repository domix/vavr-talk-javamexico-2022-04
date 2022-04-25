package vavr.talk.javamexico.business.interest.impl

import vavr.talk.javamexico.business.interest.InterestCalculationContext
import vavr.talk.javamexico.investing.InvestingAccount
import vavr.talk.javamexico.investing.InvestingUser

class DefaultInterestCalculationIntegrationSpecs extends BaseDBIntegrationSpec {

  def foo2() {
    given:
      def underTest = new DefaultInterestCalculation(accountRepository, movementRepository)
      def calculationContext = InterestCalculationContext.builder()
        .contracts(contractRepository.findAll().get())
        .build()
    and:
      def user = InvestingUser.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .email(faker.internet().emailAddress())
        .build()
      def savedUser = userRepository.save(user).get()
      def contract = contractRepository.findAll().get().get(0)
      def account = InvestingAccount.builder()
        .userId(savedUser.id)
        .contractId(contract.id)
        .startBalance(20_000)
        .currentBalance(20_000)
        .build()
      def savedAccount = accountRepository.save(account)
        .get()
      accountRepository.save(account)
    when:
      def result = underTest.process(calculationContext, savedUser)
    then:
      result.empty
  }
}
