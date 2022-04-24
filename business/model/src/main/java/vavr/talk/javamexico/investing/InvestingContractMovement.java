package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class InvestingContractMovement {

    Long id;

    @PositiveOrZero
    long accountId;

    @NotBlank
    String movementType;

    @PositiveOrZero
    BigDecimal amount;

    OffsetDateTime created;
    OffsetDateTime updated;

}
