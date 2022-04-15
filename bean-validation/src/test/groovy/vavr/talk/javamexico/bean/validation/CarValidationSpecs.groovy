package vavr.talk.javamexico.bean.validation

import jakarta.validation.Validation
import jakarta.validation.Validator
import spock.lang.Specification

class CarValidationSpecs extends Specification {

  Validator validator

  def setup() {
    validator = Validation.buildDefaultValidatorFactory().validator
  }

  def 'should validate a Car: "#scenario"'() {
    given:
      def car = Car.builder()
        .manufacturer(manufacturer)
        .licensePlate(licencePlate)
        .seatCount(seatCount)
        .build()
    when:
      def constraintViolations = validator.validate(car)
    then:
      constraintViolations.size() == violationCount
      if (expectedMessage) {
        constraintViolations.iterator().next().message == expectedMessage
      }
    where:
      scenario                  | manufacturer | licencePlate | seatCount || violationCount || expectedMessage
      'car is valid'            | 'Morris'     | 'DD-AB-123'  | 2         || 0              || null
      'seat count too low'      | 'Morris'     | 'DD-AB-123'  | 1         || 1              || 'must be greater than or equal to 2'
      'license plate too short' | 'Morris'     | 'D'          | 4         || 1              || 'size must be between 2 and 14'
      'manufacturer is null'    | null         | 'DD-AB-123'  | 4         || 1              || 'must not be null'
  }
}
