package vavr.talk.javamexico.business.interest;

import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingUser;

import java.util.Optional;

public interface InterestCalculation {
  Optional<Failure> process(InterestCalculationContext context, InvestingUser user);

}
