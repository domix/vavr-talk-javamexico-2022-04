package vavr.talk.javamexico.validation.javax

import spock.lang.Specification
import vavr.talk.javamexico.Failure
import vavr.talk.javamexico.validation.BeanValidator

class JavaxBeanValidationSpecs extends Specification {
  def 'should validate the expected behaviour'() {
    given: 'An initial context'
      BeanValidator validator = JavaxBeanValidation.builder().build()
      SampleBean bean = SampleBean.builder().build()
    when: 'a bean has 4 invalid property values'
      def invalidProperties = validator.validateBean(bean)
    then: 'we should get exactly 4 details'
      invalidProperties.isLeft()
      def failure = invalidProperties.getLeft()
      failure.cause.isEmpty()
      failure.code
      failure.details
      failure.reason
      failure.i18nData
      failure.details.size() == 4
      failure.i18nData.code
      failure.i18nData.defaultMessage
      def detail = failure.details.get(0)
      detail.localizedMessage
      detail.codeMessage
      detail.path
      detail.type == Failure.ErrorType.VALIDATION
    when: 'the bean has 3 invalid property values'
      bean = SampleBean.builder().name('foo').build()
      invalidProperties = validator.validateBean(bean)
    then: 'we should get exactly 3 details'
      invalidProperties.getLeft().getDetails().size() == 3
    when: 'the bean has valid property values'
      bean = SampleBean.builder().name('foo').manufacturer('bar').licensePlate('bazz').seatCount(2).build()
      invalidProperties = validator.validateBean(bean)
    then: 'the Failure is not present'
      invalidProperties.isRight()
  }
}
