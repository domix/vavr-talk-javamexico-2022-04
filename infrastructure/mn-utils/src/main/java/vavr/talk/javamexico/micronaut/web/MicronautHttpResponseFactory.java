package vavr.talk.javamexico.micronaut.web;

import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.MutableHttpResponse;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.Failure;

import javax.annotation.Nonnull;
import java.util.function.Function;

@Slf4j
public class MicronautHttpResponseFactory {
  @Nonnull
  public static <Body> MutableHttpResponse<?> ok(@Nonnull Either<Failure, Body> either) {
    return either
      .peekLeft(failure -> failure.getCause()
        .peek(throwable -> log.error(throwable.getMessage(), throwable))
        .onEmpty(() -> log.warn(failure.getReason())))
      .map(HttpResponseFactory.INSTANCE::ok)
      .mapLeft(HttpResponseFailureMapper::map)
      .fold(Function.identity(), Function.identity());
  }
}
