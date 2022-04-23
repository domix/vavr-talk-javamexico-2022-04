package vavr.talk.javamexico.business.investing;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * The main function of this table is to keep track of the calculation of accrued interest for the period
 * assigned in the {@link InvestingTerm}
 */
@Value
@Builder
public class InvestingUserInterest {

    long id;

    long userId;

    long investingTermId;

    String currency;

    BigDecimal startBalance;

    BigDecimal averageBalance;

    BigDecimal endBalance;

    BigDecimal interest;

    BigDecimal accruedInterest;

}
