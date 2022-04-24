package vavr.talk.javamexico.business.interest;

import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.InvestingUser;

import java.math.BigDecimal;
import java.util.Optional;

public interface InterestCalculation {
  //BigDecimal interestFor(InvestingAccount account, InvestingContract contract);
  BigDecimal interestFor(InvestingUser user);

  Optional<Failure> process(InterestCalculationContext context, Long userId);

}
