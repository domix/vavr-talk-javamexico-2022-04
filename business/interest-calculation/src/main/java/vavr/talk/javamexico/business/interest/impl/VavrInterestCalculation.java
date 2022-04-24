package vavr.talk.javamexico.business.interest.impl;

import io.vavr.concurrent.Future;
import lombok.RequiredArgsConstructor;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.business.interest.InterestCalculation;
import vavr.talk.javamexico.business.interest.InterestCalculationContext;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.repository.InvestingAccountRepository;
import vavr.talk.javamexico.repository.InvestingContractRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@RequiredArgsConstructor
public class VavrInterestCalculation implements InterestCalculation {

  private final InvestingAccountRepository accountRepository;
  private final InvestingContractRepository contractRepository;
  private final ExecutorService executorService;

  @Override
  public BigDecimal interestFor(final InvestingUser user) {
    return accountRepository.findAllActiveAccounts(user.getId())
      .fold(__ -> BigDecimal.ZERO,
        this::calculateInterestForInvestingUser);
  }

  @Override
  public Optional<Failure> process(InterestCalculationContext context, Long userId) {
    return Optional.empty();
  }

  private BigDecimal calculateInterestForInvestingUser(final List<InvestingAccount> accounts) {
    final Function<InvestingAccount, BigDecimal> call = this::calculateInterestForContractAndAccount;
    final var accountFutures = accounts
      .stream()
      .map(account -> Future.of(executorService, () -> call.apply(account)))
      .toList();
    return Future.sequence(executorService, accountFutures)
      .await()
      .getOrElse(io.vavr.collection.List.empty())
      .fold(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal calculateInterestForContractAndAccount(final InvestingAccount account) {
    final var savedContract = contractRepository.find(account.getContractId());
    return savedContract.fold(__ -> BigDecimal.ZERO,
      contract -> {
        final var mathContext = new MathContext(2);
        final var balance = (BigDecimal.ZERO.equals(account.getCurrentBalance())) ?
          account.getStartBalance() : account.getCurrentBalance();
        return balance
          .multiply(new BigDecimal(contract.getAnnualInterestRate()), mathContext)
          .divide(new BigDecimal("100.00"), mathContext);
      });
  }

}
