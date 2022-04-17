package vavr.talk.javamexico.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.business.BusinessContract;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
  private final BusinessContract businessContract;

  @Get
  public HttpResponse<?> index() {
    return businessContract.someProcess()
      .peek(greeting -> log.info("Greeting: {}", greeting.getMessage()))
      .peekLeft(failure -> failure.getCause()
        .peek(throwable -> log.error(throwable.getMessage(), throwable))
        .onEmpty(() -> log.warn(failure.getReason())))
      .map(HttpResponseFactory.INSTANCE::ok)
      .mapLeft(failure -> HttpResponseFactory.INSTANCE.status(HttpStatus.BAD_REQUEST, Map.of("error", failure.getReason())))
      .fold(Function.identity(), Function.identity());
  }
}
