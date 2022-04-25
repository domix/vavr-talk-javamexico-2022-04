package vavr.talk.javamexico.business.interest.impl;

import lombok.RequiredArgsConstructor;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.business.interest.InterestCalculationContext;
import vavr.talk.javamexico.business.interest.InterestCalculationProcess;
import vavr.talk.javamexico.repository.InvestingContractRepository;
import vavr.talk.javamexico.repository.InvestingUserRepository;

import java.util.Optional;

@RequiredArgsConstructor
public class SequentialInterestCalculationProcess implements InterestCalculationProcess {
  private final InvestingUserRepository userRepository;
  private final InvestingContractRepository contractRepository;
  private final InterestCalculation interestCalculation;

  @Override
  public Optional<Failure> start() {
    final var map = contractRepository.findAll()
      .map(investingContracts -> InterestCalculationContext.builder()
        .contracts(investingContracts)
        .build())
      .map(context -> {
        return userRepository.streamAll()
          .map(investingUserStream -> {
            return investingUserStream.map(investingUser -> {
              return interestCalculation.process(context, investingUser.getId());
            });
          });

      });

    return Optional.empty();
  }
}
