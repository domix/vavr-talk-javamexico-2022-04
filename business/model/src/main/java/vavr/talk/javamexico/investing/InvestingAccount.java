package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@ToString
@Value
@Builder
public class InvestingAccount {

    Long id;

    @PositiveOrZero
    long contractId;

    String status;

    @PositiveOrZero
    BigDecimal startBalance;

    @PositiveOrZero
    BigDecimal currentBalance;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

}
