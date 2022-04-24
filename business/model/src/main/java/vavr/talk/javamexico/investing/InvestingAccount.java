package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Value
@Builder
public class InvestingAccount {

  Long id;

  @PositiveOrZero
  long contractId;

  @Builder.Default
  String status = "open";

  @Builder.Default
  @PositiveOrZero
  BigDecimal startBalance = BigDecimal.ZERO;

  @Builder.Default
  @PositiveOrZero
  BigDecimal currentBalance = BigDecimal.ZERO;

  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;

}
