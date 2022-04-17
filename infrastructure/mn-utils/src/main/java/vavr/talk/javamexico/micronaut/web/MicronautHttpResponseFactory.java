package vavr.talk.javamexico.micronaut.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.vavr.control.Either;
import vavr.talk.javamexico.Failure;

import java.util.function.Function;

public class MicronautHttpResponseFactory {
  public static <Body> HttpResponse<?> ok(Either<Failure, Body> either) {
    return either
      .map(HttpResponseFactory.INSTANCE::ok)
      .mapLeft(failure -> {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.BAD_REQUEST, failure);
      })
      .fold(Function.identity(), Function.identity());
  }
}
