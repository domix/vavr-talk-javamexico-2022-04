package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContract;

public interface InvestingContractRepository {
    Either<Failure, InvestingContract> save(InvestingContract investingContract);
}
