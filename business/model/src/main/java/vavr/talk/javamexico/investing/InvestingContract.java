package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class InvestingContract {

    Long id;

    @NotBlank
    String contractName;

    @NotBlank
    String currency;

    @NotBlank
    String annualInterestRate;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

}
