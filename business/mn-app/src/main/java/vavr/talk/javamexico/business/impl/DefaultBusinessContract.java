package vavr.talk.javamexico.business.impl;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.RandomUtils;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.business.BusinessContract;
import vavr.talk.javamexico.business.Greeting;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

@Singleton
public class DefaultBusinessContract implements BusinessContract {

  @Nonnull
  @Override
  public Either<Failure, Greeting> someProcess() {
    return randomChoice(
      () -> Either.left(buildFailure()),
      () -> Either.right(Greeting.of("hola"))
    );
  }

  @Nonnull
  private Failure buildFailure() {
    return randomChoice(
      () -> Failure.of(new RuntimeException("because"), "business.errorCode1", "Unexpected Error"),
      () -> Failure.of("business.errorCode2", "Business Error..")
    );
  }

  @Nonnull
  private <Value> Value randomChoice(@Nonnull Supplier<Value> right, @Nonnull Supplier<Value> left) {
    return Optional.of(RandomUtils.nextBoolean())
      .filter(__ -> __)
      .map(__ -> right.get())
      .orElse(left.get());
  }
}
