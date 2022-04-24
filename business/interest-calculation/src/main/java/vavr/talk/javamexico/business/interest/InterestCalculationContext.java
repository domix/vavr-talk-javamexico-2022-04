package vavr.talk.javamexico.business.interest;

import lombok.Builder;
import lombok.Getter;
import vavr.talk.javamexico.investing.InvestingContract;

import java.util.List;

@Builder
@Getter
public class InterestCalculationContext {
  private List<InvestingContract> contracts;
}
