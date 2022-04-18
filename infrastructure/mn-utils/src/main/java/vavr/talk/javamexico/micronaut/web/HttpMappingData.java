package vavr.talk.javamexico.micronaut.web;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class HttpMappingData {
  @Builder.Default
  final Integer httpStatusCode = 400;
  @Builder.Default
  final FailureMapper<?> mapper = HttpResponseFailureMapper.defaultMapper;
}
