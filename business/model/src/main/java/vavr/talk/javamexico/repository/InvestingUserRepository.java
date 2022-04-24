package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.InvestingUser;

public interface InvestingUserRepository {

    Either<Failure, InvestingUser> save(InvestingUser investingUser);

    Either<Failure, InvestingUser> get(long userId);

}
