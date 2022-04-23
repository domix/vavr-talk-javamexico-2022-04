package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

<<<<<<< HEAD
import java.time.OffsetDateTime;

=======
>>>>>>> b0113d7 (Moving models entities to model module)
@Value
@Builder
public class InvestingTerm {

    Long id;

    @NotNull
    TermPeriod calculationPeriod;
<<<<<<< HEAD
    
    OffsetDateTime created;
    OffsetDateTime updated;
=======
>>>>>>> b0113d7 (Moving models entities to model module)

    public enum TermPeriod {
        WEEKLY,
        MONTHLY
    }

}
