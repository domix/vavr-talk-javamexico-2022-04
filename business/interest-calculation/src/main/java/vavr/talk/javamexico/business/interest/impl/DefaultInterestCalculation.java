package vavr.talk.javamexico.business.interest.impl;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Value;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.business.interest.InterestCalculationContext;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.investing.InvestingContractMovement;
import vavr.talk.javamexico.repository.InvestingAccountRepository;
import vavr.talk.javamexico.repository.InvestingContractMovementRepository;
import vavr.talk.javamexico.repository.InvestingContractRepository;
import vavr.talk.javamexico.repository.InvestingUserRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultInterestCalculation implements InterestCalculation {
  private final InvestingUserRepository userRepository;
  private final InvestingAccountRepository accountRepository;
  //private final InvestingContractRepository contractRepository;
  //private final InvestingContractMovementRepository movementRepository;

  @Override
  public Optional<Failure> process(InterestCalculationContext context, Long userId) {
    return userRepository.find(userId)
      .flatMap(this::calculationDataForUser)
      .map(data -> data.append(context))
      .map(this::calculateInterestFor)
      .peek(investingContractMovements -> {
        //guardar datos
      })
      .fold(Optional::of, __ -> Optional.<Failure>empty());
  }

  private Either<Failure, Tuple2<InvestingUser, List<InvestingAccount>>> calculationDataForUser(InvestingUser user) {
    return accountRepository.findAllActiveAccounts(user.getId())
      .map(investingAccounts -> Tuple.of(user, investingAccounts));
  }

  private BigDecimal computeInterestFor(InvestingContract contract, InvestingAccount account) {
    final var mathContext = new MathContext(2);
    final var balance = (BigDecimal.ZERO.equals(account.getCurrentBalance())) ?
      account.getStartBalance() : account.getCurrentBalance();
    return balance
      .multiply(new BigDecimal(contract.getAnnualInterestRate()), mathContext)
      .divide(new BigDecimal("100.00"), mathContext);
  }

  private Option<InvestingContract> contractFor(InvestingAccount account, InterestCalculationContext context) {
    return Option.ofOptional(context.getContracts().stream()
      .filter(contract -> Objects.equals(contract.getId(), account.getContractId()))
      .findFirst());
  }

  private List<InvestingContractMovement> calculateInterestFor(Tuple3<InvestingUser, List<InvestingAccount>, InterestCalculationContext> data) {
    return data._2.stream()
      .map(account -> createMovementIfNeeded(data, account))
      .map(Value::toJavaOptional)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toList();
  }

  private Option<InvestingContractMovement> createMovementIfNeeded(
    Tuple3<InvestingUser, List<InvestingAccount>, InterestCalculationContext> data, InvestingAccount account
  ) {
    return contractFor(account, data._3)
      .onEmpty(() -> log.warn("No se encontro contrato de la cuenta {}", account.getId()))
      .map(contract -> createAccountMovement(account, contract));
  }

  private InvestingContractMovement createAccountMovement(InvestingAccount account, InvestingContract contract) {
    return InvestingContractMovement.builder()
      .accountId(account.getId())
      .amount(computeInterestFor(contract, account))
      .movementType("interest")
      .build();
  }
}
