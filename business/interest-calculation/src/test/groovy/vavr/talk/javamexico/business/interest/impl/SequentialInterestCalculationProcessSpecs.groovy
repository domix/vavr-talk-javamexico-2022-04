package vavr.talk.javamexico.business.interest.impl

import org.apache.commons.lang3.time.StopWatch
import vavr.talk.javamexico.business.interest.InterestCalculation
import vavr.talk.javamexico.business.interest.InterestCalculationProcess

class SequentialInterestCalculationProcessSpecs extends BaseDBIntegrationSpec {

  def foo() {
    given:
      InterestCalculation interestCalculation = new DefaultInterestCalculation(accountRepository, movementRepository)
      InterestCalculationProcess underTest = new SequentialInterestCalculationProcess(userRepository, contractRepository, interestCalculation)
      def watch = new StopWatch()
      watch.start()
    when:
      underTest.start()
      watch.stop()
    then:
      underTest
      println "Tiempo de procesamiento: ${ watch.toString() }"
  }
}
