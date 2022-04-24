package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingAccount;

import java.util.List;
import java.util.stream.Stream;

public interface InvestingAccountRepository {

  Either<Failure, List<InvestingAccount>> findAllByUserId(long userId);

  Either<Failure, List<InvestingAccount>> findAllActiveAccounts(long userId);

  Either<Failure, InvestingAccount> save(InvestingAccount investingAccount);

  Either<Failure, InvestingAccount> get(long id);

  Either<Failure, Stream<InvestingAccount>> streamAllActiveAccounts(long userId);

  Either<Failure, InvestingAccount> update(InvestingAccount investingAccount);

}
