package vavr.talk.javamexico.validation;

import vavr.talk.javamexico.Failure;
import io.vavr.control.Either;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class BeanValidatorUtil {
  @Nonnull
  public static String orEmpty(final @Nullable String value) {
    return Optional.ofNullable(value).orElse("");
  }

  @Nonnull
  public static <T> Either<Failure, T> wrapFailure(final @Nonnull Failure failure, T ignored) {
    return Either.left(failure);
  }
}
