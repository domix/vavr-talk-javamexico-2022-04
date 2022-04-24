package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Value
@Builder
public class InvestingContractMovement {

  Long id;

  @PositiveOrZero
  long accountId;

  @NotBlank
  String movementType;

  @PositiveOrZero
  @Builder.Default
  BigDecimal amount = BigDecimal.ZERO;

  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;

}
