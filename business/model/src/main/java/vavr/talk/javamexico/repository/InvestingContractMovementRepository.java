package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContractMovement;

public interface InvestingContractMovementRepository {
  Either<Failure, InvestingContractMovement> create(InvestingContractMovement movement);
}
