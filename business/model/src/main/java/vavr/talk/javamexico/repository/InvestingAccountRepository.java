package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingAccount;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface InvestingAccountRepository {

  Either<Failure, List<InvestingAccount>> findAllByUserId(long userId);

  Either<Failure, List<InvestingAccount>> findAllActiveAccounts(long userId);

  Either<Failure, InvestingAccount> save(InvestingAccount investingAccount);

  Either<Failure, InvestingAccount> find(long id);

  Either<Failure, Stream<InvestingAccount>> streamAllActiveAccounts(long userId);

  Either<Failure, InvestingAccount> update(InvestingAccount investingAccount);

  Optional<Failure> updateBatch(List<InvestingAccount> batch);

  <T> Either<Failure, T> executeInTransaction(Supplier<Either<Failure, T>> operation);

}
