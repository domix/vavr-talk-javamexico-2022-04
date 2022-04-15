package vavr.talk.javamexico.bean.validation

import jakarta.validation.Validation
import jakarta.validation.Validator
import spock.lang.Specification

class CarValidationSpecs extends Specification {
  static Validator validator

  def setup() {
    validator = Validation.buildDefaultValidatorFactory().validator
  }

  def foo() {
    given:
      def car = Car.builder()
        .licensePlate("DD-AB-123")
        .seatCount(4)
        .build()
    when:
      def constraintViolations = validator.validate(car)
    then:
      constraintViolations.size() == 1
      constraintViolations.iterator().next().message == 'must not be null'
  }

  def bar() {
    given:
      def car = Car.builder()
        .manufacturer("Morris")
        .licensePlate("D")
        .seatCount(4)
        .build()
    when:
      def constraintViolations = validator.validate(car)
    then:
      constraintViolations.size() == 1
      constraintViolations.iterator().next().message == 'size must be between 2 and 14'
  }
}
