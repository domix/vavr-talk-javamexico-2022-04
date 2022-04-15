package vavr.talk.javamexico.validation.javax

import spock.lang.Specification
import vavr.talk.javamexico.validation.BeanValidator
import vavr.talk.javamexico.validation.ValidationApiTCK

class JavaxBeanValidationSpecs extends Specification {

  def 'should validate the expected behaviour for "#scenario"'() {
    given: 'A validator from Javax'
      BeanValidator validator = JavaxBeanValidation.ofDefaults()
    when: 'try to pass the TCK'
      def passTCK = ValidationApiTCK.validate(validator, bean, errors)
    then: 'we should pass the TCK'
      passTCK
    where:
      scenario    | bean                                                                                           | errors
      '4 errors'  | SampleBean.builder().build()                                                                   | 4
      '3 errors'  | SampleBean.builder().name('foo').build()                                                       | 3
      'no errors' | SampleBean.builder().name('foo').manufacturer('bar').licensePlate('bazz').seatCount(2).build() | 0
  }
}
