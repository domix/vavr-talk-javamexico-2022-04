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
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.business.interest.InterestCalculationContext;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.investing.InvestingContractMovement;
import vavr.talk.javamexico.investing.InvestingUser;
import vavr.talk.javamexico.repository.InvestingAccountRepository;
import vavr.talk.javamexico.repository.InvestingContractMovementRepository;
import vavr.talk.javamexico.repository.InvestingUserRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultInterestCalculation implements InterestCalculation {
  private final InvestingAccountRepository accountRepository;
  private final InvestingContractMovementRepository movementRepository;

  @Override
  public Optional<Failure> process(InterestCalculationContext context, InvestingUser user) {
    return calculationDataForUser(user)
      .map(data -> data.append(context))
      .map(this::calculateInterestFor)
      .peek(movements -> {
        log.info("Movimientos: {}", movements.size());

        final var accountNewAmounts = new HashMap<Long, BigDecimal>();
        movements.forEach(data -> {
          final var account = data._1;
          final var key = account.getId();
          final var movement = data._2;
          BigDecimal amount = movement.getAmount();

          if (accountNewAmounts.containsKey(key)) {
            amount = amount.add(accountNewAmounts.get(key));
          }
          accountNewAmounts.put(key, amount);
        });

        log.info("data: {}", accountNewAmounts);
        final var investingAccounts = movements.stream()
          .map(moves -> moves._1)
          .filter(account -> accountNewAmounts.containsKey(account.getId()))
          .map(account -> getBuild(accountNewAmounts, account))
          .toList();

        //guardar datos
        accountRepository.updateBatch(investingAccounts).ifPresent(failure -> {
          log.error(failure.toString());
        });

        final var investingContractMovements = movements.stream()
          .map(movs -> movs._2)
          .toList();
        movementRepository.insertBatch(investingContractMovements).ifPresent(failure -> {
          log.error(failure.toString());
        });

      })
      .fold(Optional::of, __ -> Optional.empty());
  }

  private InvestingAccount getBuild(HashMap<Long, BigDecimal> accountNewAmounts, InvestingAccount account) {
    return InvestingAccount.builder()
      .contractId(account.getContractId())
      .id(account.getId())
      .currentBalance(account.getCurrentBalance().add(accountNewAmounts.get(account.getId())))
      .startBalance(account.getStartBalance())
      .status(account.getStatus())
      .userId(account.getUserId())
      .build();
  }

  private Either<Failure, Tuple2<InvestingUser, List<InvestingAccount>>> calculationDataForUser(InvestingUser user) {
    return accountRepository.findAllActiveAccounts(user.getId())
      .map(investingAccounts -> Tuple.of(user, investingAccounts));
  }

  private BigDecimal computeInterestFor(InvestingContract contract, InvestingAccount account) {
    final var template = """
      Calculando intereses del producto: '{}' para la cuenta '{}'
         Moneda:  {}
         APY:     {}
         Saldo:   {}
         Balance: {}
         Interes: {}
      """;

    final var mathContext = new MathContext(8);
    final var balance = (BigDecimal.ZERO.equals(account.getCurrentBalance())) ?
      account.getStartBalance() : account.getCurrentBalance();
    final var annualInterestRate = new BigDecimal(contract.getAnnualInterestRate())
      .divide(new BigDecimal("365"), mathContext);
    final var divide = balance
      .multiply(annualInterestRate, mathContext)
      .divide(new BigDecimal("100.00"), mathContext);
    log.info(template, contract.getContractName(), account.getId(), contract.getCurrency(), contract.getAnnualInterestRate(), account.getCurrentBalance(), balance, divide.toPlainString());
    return divide;
  }

  private Option<InvestingContract> contractFor(InvestingAccount account, InterestCalculationContext context) {
    return Option.ofOptional(context.getContracts().stream()
      .filter(contract -> Objects.equals(contract.getId(), account.getContractId()))
      .findFirst());
  }

  private List<Tuple2<InvestingAccount, InvestingContractMovement>> calculateInterestFor(Tuple3<InvestingUser, List<InvestingAccount>, InterestCalculationContext> data) {
    return data._2.stream()
      .map(account -> createMovementIfNeeded(data, account))
      .map(Value::toJavaOptional)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toList();
  }

  private Option<Tuple2<InvestingAccount, InvestingContractMovement>> createMovementIfNeeded(
    Tuple3<InvestingUser, List<InvestingAccount>, InterestCalculationContext> data, InvestingAccount account
  ) {
    return contractFor(account, data._3)
      .onEmpty(() -> log.warn("No se encontro contrato de la cuenta {}", account.getId()))
      .map(contract -> createAccountMovement(account, contract));
  }

  private Tuple2<InvestingAccount, InvestingContractMovement> createAccountMovement(InvestingAccount account, InvestingContract contract) {
    final var interest = InvestingContractMovement.builder()
      .accountId(account.getId())
      .amount(computeInterestFor(contract, account))
      .movementType("interest")
      .build();

    return Tuple.of(account, interest);
  }
}
