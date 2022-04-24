package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContract;

import java.util.List;
import java.util.stream.Stream;

public interface InvestingContractRepository {

  Either<Failure, InvestingContract> save(InvestingContract investingContract);

  Either<Failure, InvestingContract> find(long id);

  Either<Failure, List<InvestingContract>> findAll();

  Either<Failure, Stream<InvestingContract>> streamAll();

  Either<Failure, InvestingContract> update(InvestingContract investingContract);

}
