package vavr.talk.javamexico.business.impl;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.RandomUtils;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.business.BusinessContract;
import vavr.talk.javamexico.business.Greeting;

import javax.annotation.Nonnull;

@Singleton
public class DefaultBusinessContract implements BusinessContract {

  @Nonnull
  @Override
  public Either<Failure, Greeting> someProcess() {
    if (RandomUtils.nextBoolean()) {
      if (RandomUtils.nextBoolean()) {
        return Either.left(Failure.of(new RuntimeException("because"), "business.errorCode1", "Unexpected Error"));
      }
      return Either.left(Failure.of("business.errorCode2", "Business Error.."));
    }

    return Either.right(Greeting.of("hola"));
  }
}
