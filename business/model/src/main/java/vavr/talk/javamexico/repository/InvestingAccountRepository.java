package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingAccount;

import java.util.List;

public interface InvestingAccountRepository {

  Either<Failure, InvestingAccount> create(InvestingAccount account);

  Either<Failure, InvestingAccount> update(InvestingAccount account);

  Either<Failure, List<InvestingAccount>> findAllByUserId(long userId);

  Either<Failure, List<InvestingAccount>> findAllActiveAccounts(final long userId);

}
