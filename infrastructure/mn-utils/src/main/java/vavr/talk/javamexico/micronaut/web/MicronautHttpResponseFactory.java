package vavr.talk.javamexico.micronaut.web;

import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.MutableHttpResponse;
import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class MicronautHttpResponseFactory {
  @Nonnull
  public static <Body> MutableHttpResponse<?> ok(@Nonnull Either<Failure, Body> either) {
    return either
      .map(HttpResponseFactory.INSTANCE::ok)
      .mapLeft(HttpResponseFailureMapper::map)
      .fold(Function.identity(), Function.identity());
  }
}
