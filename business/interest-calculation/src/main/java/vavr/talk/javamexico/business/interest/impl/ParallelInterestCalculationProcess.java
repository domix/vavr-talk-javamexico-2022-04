package vavr.talk.javamexico.business.interest.impl;

import io.vavr.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.business.interest.InterestCalculationContext;
import vavr.talk.javamexico.business.interest.InterestCalculationProcess;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.repository.InvestingContractRepository;
import vavr.talk.javamexico.repository.InvestingUserRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class ParallelInterestCalculationProcess implements InterestCalculationProcess {

  private final InvestingUserRepository userRepository;
  private final InvestingContractRepository contractRepository;
  private final InterestCalculation interestCalculation;

  private final ExecutorService executor = Executors.newFixedThreadPool(8);

  @Override
  public void start() {
    contractRepository.findAll()
      .map(this::buildContext)
      .peek(this::process)
      .peekLeft(failure -> log.error("Failure: {}", failure));
  }

  private void process(InterestCalculationContext context) {
    userRepository.streamAll()
      .map(userStream -> userStream
        .map(user -> Future.of(executor, () -> interestCalculation.process(context, user)))
        .toList()
      ).map(futures -> Future.sequence(executor, futures)
        .recover(throwable -> io.vavr.collection.List.empty())
        .await()
        .onComplete(__ -> log.info("Listo")));
  }

  private InterestCalculationContext buildContext(List<InvestingContract> investingContracts) {
    return InterestCalculationContext.builder()
      .contracts(investingContracts)
      .build();
  }
}
