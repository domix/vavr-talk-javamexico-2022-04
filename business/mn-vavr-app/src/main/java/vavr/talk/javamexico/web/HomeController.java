package vavr.talk.javamexico.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.business.BusinessContract;
import vavr.talk.javamexico.micronaut.web.MicronautHttpResponseFactory;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
  private final BusinessContract businessContract;

  @Get
  public HttpResponse<?> index() {

    final var greetings = businessContract.someProcess()
      .peek(greeting -> log.info("Greeting: {}", greeting.getMessage()));

    return MicronautHttpResponseFactory.ok(greetings);
  }
}
