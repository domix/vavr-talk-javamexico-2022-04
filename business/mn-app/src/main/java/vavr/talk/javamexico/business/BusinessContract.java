package vavr.talk.javamexico.business;

import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;

import javax.annotation.Nonnull;

public interface BusinessContract {
  @Nonnull
  Either<Failure, Greeting> someProcess();
}
