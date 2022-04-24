package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContract;

public interface InvestingContractRepository {
    Either<Failure, InvestingContract> save(InvestingContract investingContract);

    Either<Failure, InvestingContract> get(long id);
}
