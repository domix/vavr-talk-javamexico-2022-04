package vavr.talk.javamexico.business.interest.impl;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.investing.InvestingContract;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DefaultInterestCalculation implements InterestCalculation {
  @SneakyThrows
  @Override
  public BigDecimal interestFor(InvestingAccount account, InvestingContract contract) {
    BigDecimal bigDecimal = new BigDecimal(contract.getAnnualInterestRate());


    Calendar calOne = Calendar.getInstance();
    int dayOfYear = calOne.get(Calendar.DAY_OF_YEAR);
    int year = calOne.get(Calendar.YEAR);
    Calendar calTwo = new GregorianCalendar(year, 11, 31);
    int day = calTwo.get(Calendar.DAY_OF_YEAR);
    System.out.println("Days in current year: " + day);


    Thread.sleep(RandomUtils.nextInt(20, 50));


    return bigDecimal;
  }
}
