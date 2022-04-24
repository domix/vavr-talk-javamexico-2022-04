package vavr.talk.javamexico.business.interest.impl

import spock.lang.Specification

class DefaultInterestCalculationSpecs extends Specification {

  def foo() {
    given:
      def underTest = new DefaultInterestCalculation(null)
    when:
      1000.times {
        underTest.interestFor(null)
      }
      def interestFor = underTest.interestFor(null)
    then:
      !interestFor
  }
}
