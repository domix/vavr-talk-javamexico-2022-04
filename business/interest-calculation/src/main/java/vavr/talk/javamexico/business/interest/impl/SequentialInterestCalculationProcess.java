package vavr.talk.javamexico.business.interest.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.business.interest.InterestCalculationContext;
import vavr.talk.javamexico.business.interest.InterestCalculationProcess;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.repository.InvestingContractRepository;
import vavr.talk.javamexico.repository.InvestingUserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SequentialInterestCalculationProcess implements InterestCalculationProcess {
  private final InvestingUserRepository userRepository;
  private final InvestingContractRepository contractRepository;
  private final InterestCalculation interestCalculation;

  @Override
  public void start() {
    contractRepository.findAll()
      .map(this::buildContext)
      .peek(this::process)
      .peekLeft(failure -> log.error("Failure: {}", failure));
  }

  private void process(InterestCalculationContext context) {
    userRepository.streamAll()
      .peek(userStream -> userStream
        .peek(investingUser -> {
          log.info("User: {}", investingUser.getFirstName());
        })
        //.map(user -> interestCalculation.process(context, user))
        //.filter(Optional::isPresent)
        .forEach(failure -> log.error("Failure: {}", failure)));
  }

  private InterestCalculationContext buildContext(List<InvestingContract> investingContracts) {
    return InterestCalculationContext.builder()
      .contracts(investingContracts)
      .build();
  }
}
