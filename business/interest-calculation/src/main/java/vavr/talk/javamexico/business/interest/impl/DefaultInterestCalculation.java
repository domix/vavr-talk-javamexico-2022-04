package vavr.talk.javamexico.business.interest.impl;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultInterestCalculation implements InterestCalculation {
  private final InvestingUserRepository userRepository;
  private final InvestingAccountRepository accountRepository;
  private final InvestingContractRepository contractRepository;
  private final InvestingContractMovementRepository movementRepository;


  @SneakyThrows
  @Override
  public BigDecimal interestFor(InvestingUser user) {
    final var allAccountsByUserId = accountRepository.findAllActiveAccounts(user.getId())
      .map(investingAccounts -> {
        investingAccounts.stream()
          .map(investingAccount -> {
            return "";
          });
        return "";
      });
    //BigDecimal bigDecimal = new BigDecimal(contract.getAnnualInterestRate());

    Calendar calOne = Calendar.getInstance();
    int year = calOne.get(Calendar.YEAR);
    Calendar calTwo = new GregorianCalendar(year, 11, 31);
    int day = calTwo.get(Calendar.DAY_OF_YEAR);
    System.out.println("Days in current year: " + day);
    Thread.sleep(RandomUtils.nextInt(20, 50));
    return null;
  }

  @Override
  public Optional<Failure> process(InterestCalculationContext context, Long userId) {
    final var investingUsers = userRepository.find(userId);

    final var map = investingUsers
      .flatMap(this::ff)
      .map(data -> data.append(context));
    return Optional.empty();
  }

  private Either<Failure, Tuple2<InvestingUser, List<InvestingAccount>>> ff(InvestingUser user) {
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

  private Optional<InvestingContract> contractFor(InvestingAccount account, InterestCalculationContext context) {
    return context.getContracts().stream()
      .filter(contract -> Objects.equals(contract.getId(), account.getContractId()))
      .findFirst();
  }

  private void calculateInterestFor(Tuple3<InvestingUser, List<InvestingAccount>, InterestCalculationContext> data) {
    data._2.stream()
      .peek(account -> {
        final var investingContract = contractFor(account, data._3);


        investingContract.ifPresentOrElse(contract -> {
          BigDecimal amount = computeInterestFor(contract, account);
          InvestingContractMovement movement = InvestingContractMovement.builder()
            .accountId(account.getId())
            .amount(amount)
            .movementType("interest")
            .build();
          movementRepository.create(movement);


        }, () -> {
          log.warn("No se pudo encontrar un contrato para la cuenta {}", account.getId());
        });
      });

    data._2.stream()
      .map(Tuple::of)
      .map(account -> {
        final var first = data._3.getContracts().stream()
          .filter(contract -> {
            return Objects.equals(contract.getId(), account._1.getContractId());
          }).map(contract -> {
            return contract;
          });
        return account;
      });

    //final var savedContract = contractRepository.get(account.getContractId());
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
