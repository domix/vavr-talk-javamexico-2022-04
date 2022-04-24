package vavr.talk.javamexico.business.interest;

import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.investing.InvestingContract;

import java.math.BigDecimal;

public interface InterestCalculation {
  //BigDecimal interestFor(InvestingAccount account, InvestingContract contract);
  BigDecimal interestFor(InvestingUser user);

}
