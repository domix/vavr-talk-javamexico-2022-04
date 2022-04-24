package vavr.talk.javamexico.business.interest.impl;

import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingAccountRecord;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingContractRecord;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DefaultInterestCalculation implements InterestCalculation {
  @Override
  public BigDecimal interestFor(InvestingAccountRecord account, InvestingContractRecord contract) {
    BigDecimal bigDecimal = new BigDecimal(contract.getAnnualInterestRate());


    Calendar calOne = Calendar.getInstance();
    int dayOfYear = calOne.get(Calendar.DAY_OF_YEAR);
    int year = calOne.get(Calendar.YEAR);
    Calendar calTwo = new GregorianCalendar(year, 11, 31);
    int day = calTwo.get(Calendar.DAY_OF_YEAR);
    System.out.println("Days in current year: "+day);


    return null;
  }
}
