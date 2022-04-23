package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

import java.time.OffsetDateTime;

@Value
@Builder
public class InvestingTerm {

    Long id;

    @NotNull
    TermPeriod calculationPeriod;
    
    OffsetDateTime created;
    OffsetDateTime updated;

    public enum TermPeriod {
        WEEKLY,
        MONTHLY
    }

}
