package vavr.talk.javamexico.micronaut.web;

import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import lombok.Synchronized;
import vavr.talk.javamexico.Failure;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponseFailureMapper {
  private static final ConcurrentHashMap<String, HttpMappingData> mappings = new ConcurrentHashMap<>();
  public static final FailureMapper<Map<String, String>> defaultMapper = new FailureMapper<>() {

    @Nonnull
    @Override
    public Map<String, String> map(@Nonnull Failure failure) {
      return Map.of(
        "reason", failure.getReason()
      );
    }
  };

  private static final HttpMappingData defaultMappingData = HttpMappingData.builder()
    .httpStatusCode(400)
    .mapper(defaultMapper)
    .build();

  @Synchronized
  public static void register(@Nonnull String errorCode, @Nonnull HttpMappingData httpMappingData) {
    mappings.put(errorCode, httpMappingData);
  }

  @Nonnull
  public static MutableHttpResponse<?> map(@Nonnull Failure failure) {
    final var httpMappingData = mappings.getOrDefault(failure.getCode(), defaultMappingData);
    final var httpStatus = HttpStatus.valueOf(httpMappingData.httpStatusCode);
    final var map = httpMappingData.mapper.map(failure);

    return HttpResponseFactory.INSTANCE.status(httpStatus, map);
  }
}
