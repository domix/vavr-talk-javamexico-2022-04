package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
<<<<<<< HEAD
import java.time.OffsetDateTime;
=======
>>>>>>> b0113d7 (Moving models entities to model module)

/**
 * The main function of this table is to keep track of the calculation of accrued interest for the period
 * assigned in the {@link InvestingTerm}
 */
@Value
@Builder
public class InvestingUserInterest {

    Long id;

    @PositiveOrZero
    long userId;

    @PositiveOrZero
    long investingTermId;

    @NotBlank
    String currency;

    @PositiveOrZero
    BigDecimal startBalance;

    @PositiveOrZero
    BigDecimal averageBalance;

    @PositiveOrZero
    BigDecimal endBalance;

    @PositiveOrZero
    BigDecimal interest;

    @PositiveOrZero
    BigDecimal accruedInterest;

<<<<<<< HEAD
    OffsetDateTime created;

    OffsetDateTime updated;

=======
>>>>>>> b0113d7 (Moving models entities to model module)
}
