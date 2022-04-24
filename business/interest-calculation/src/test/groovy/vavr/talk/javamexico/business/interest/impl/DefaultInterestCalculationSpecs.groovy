package vavr.talk.javamexico.business.interest.impl

import spock.lang.Specification
import vavr.talk.javamexico.InvestingUser
import vavr.talk.javamexico.persistence.db.repository.InvestingAccountDbRepository

class DefaultInterestCalculationSpecs extends Specification {

  def foo() {
    given:
      def accountDbRepository = Stub(InvestingAccountDbRepository)
      def underTest = new DefaultInterestCalculation(null, accountDbRepository)
      def user = InvestingUser.builder().id(1l).build()
    when:
      2000.times {
        //underTest.interestFor(user)
      }
      def interestFor = underTest.interestFor(user)
    then:
      !interestFor
  }
}
