package foo;

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

  /**
   * Convenient method to build an {@link Either} with the given {@link Failure}.
   * This method is useful just to prevent unnecessary cast
   *
   * @param failure the given Failure
   * @param ignored the bean class with validation errors, is ignored but required to propagate the type in the Either
   * @param <T>     the bean class type
   * @return the result
   */
  @Nonnull
  public static <T> Either<Failure, T> wrapFailure(final @Nonnull Failure failure, T ignored) {
    return Either.left(failure);
  }
}
