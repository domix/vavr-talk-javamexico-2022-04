package vavr.talk.javamexico.web;

import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.micronaut.web.FailureMapper;
import vavr.talk.javamexico.micronaut.web.HttpMappingData;
import vavr.talk.javamexico.micronaut.web.HttpResponseFailureMapper;

import javax.annotation.Nonnull;
import java.util.Map;

@Slf4j
@Singleton
public class FailureMapperConfigurer {
  @EventListener
  @Async
  public void loadConferenceData(final ApplicationStartupEvent event) {
    log.info("Configuring HttpResponseFailureMapper");

    FailureMapper<Map<String, String>> mapper = new FailureMapper<>() {
      @Nonnull
      @Override
      public Map<String, String> map(@Nonnull Failure failure) {
        final var techDetails = failure.getCause()
          .map(Throwable::getMessage)
          .getOrElse("Unknown tech detail...");

        return Map.of(
          "reason", failure.getReason(),
          "tech_details", techDetails
        );
      }
    };
    final var mappingData = HttpMappingData.builder()
      .httpStatusCode(500)
      .mapper(mapper)
      .build();

    HttpResponseFailureMapper.register("business.errorCode1", mappingData);
  }
}
