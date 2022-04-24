package vavr.talk.javamexico.business.interest.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.persistence.db.repository.InvestingAccountDbRepository;
import vavr.talk.javamexico.persistence.db.repository.InvestingUserDbRepository;
import vavr.talk.javamexico.repository.InvestingAccountRepository;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

@RequiredArgsConstructor
public class DefaultInterestCalculation implements InterestCalculation {
  private final InvestingUserDbRepository investingUserDbRepository;
  private final InvestingAccountRepository accountDbRepository;

  @SneakyThrows
  @Override
  public BigDecimal interestFor(InvestingUser user) {
    final var allAccountsByUserId = accountDbRepository.findAllActiveAccounts(user.getId())
      .map(investingAccounts -> {
        investingAccounts.stream()
          .map(investingAccount -> {
            return "";
          });
        return "";
      });
    //BigDecimal bigDecimal = new BigDecimal(contract.getAnnualInterestRate());

    Calendar calOne = Calendar.getInstance();
    int dayOfYear = calOne.get(Calendar.DAY_OF_YEAR);
    int year = calOne.get(Calendar.YEAR);
    Calendar calTwo = new GregorianCalendar(year, 11, 31);
    int day = calTwo.get(Calendar.DAY_OF_YEAR);
    System.out.println("Days in current year: " + day);


    Thread.sleep(RandomUtils.nextInt(20, 50));


    return null;
  }
}
