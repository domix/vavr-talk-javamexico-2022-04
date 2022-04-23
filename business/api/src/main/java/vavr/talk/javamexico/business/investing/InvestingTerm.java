package vavr.talk.javamexico.business.investing;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InvestingTerm {

    long id;

    TermPeriod calculationPeriod;

    public enum TermPeriod {
        WEEKLY,
        MONTHLY
    }

}
