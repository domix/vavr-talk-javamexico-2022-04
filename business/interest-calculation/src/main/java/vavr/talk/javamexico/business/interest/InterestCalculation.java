package vavr.talk.javamexico.business.interest;

import vavr.talk.javamexico.Failure;

import java.util.Optional;

public interface InterestCalculation {
  Optional<Failure> process(InterestCalculationContext context, Long userId);

}
