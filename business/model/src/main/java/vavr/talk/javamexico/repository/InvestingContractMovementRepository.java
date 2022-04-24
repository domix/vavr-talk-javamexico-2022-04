package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContractMovement;

import java.util.List;
import java.util.stream.Stream;

public interface InvestingContractMovementRepository {

  Either<Failure, InvestingContractMovement> save(InvestingContractMovement contractMovement);

  Either<Failure, InvestingContractMovement> find(long id);

  Either<Failure, List<InvestingContractMovement>> findAllByAccountId(long accountId);

  Either<Failure, Stream<InvestingContractMovement>> streamAllByAccountId(long accountId);

  Either<Failure, InvestingContractMovement> update(InvestingContractMovement contractMovement);

}
