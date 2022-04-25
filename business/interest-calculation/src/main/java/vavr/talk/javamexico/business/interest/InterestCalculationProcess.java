package vavr.talk.javamexico.business.interest;

import vavr.talk.javamexico.Failure;

import java.util.Optional;

public interface InterestCalculationProcess {
  Optional<Failure> start();
}
