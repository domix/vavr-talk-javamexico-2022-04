package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class InvestingAccount {

    Long id;

    @PositiveOrZero
    long contractId;

    @PositiveOrZero
    BigDecimal annualInterestRate;

    OffsetDateTime created;
    OffsetDateTime updated;

}
