package vavr.talk.javamexico.business.interest;

import vavr.talk.javamexico.persistence.jooq.tables.InvestingAccount;
import vavr.talk.javamexico.persistence.jooq.tables.InvestingContract;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingAccountRecord;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingContractRecord;

import java.math.BigDecimal;

public interface InterestCalculation {
  BigDecimal interestFor(InvestingAccountRecord account, InvestingContractRecord contract);
}
