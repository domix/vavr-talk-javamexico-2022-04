package vavr.talk.javamexico.repository;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingUser;

import java.util.List;
import java.util.stream.Stream;

public interface InvestingUserRepository {

  Either<Failure, InvestingUser> save(InvestingUser investingUser);

  Either<Failure, InvestingUser> find(long userId);

  Either<Failure, List<InvestingUser>> findAll();

  Either<Failure, Stream<InvestingUser>> streamAll();

  Either<Failure, InvestingUser> update(InvestingUser investingUser);

}
