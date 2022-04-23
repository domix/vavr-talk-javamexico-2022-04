package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InvestingTerm {

    Long id;

    @NotNull
    TermPeriod calculationPeriod;

    public enum TermPeriod {
        WEEKLY,
        MONTHLY
    }

}
