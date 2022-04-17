package vavr.talk.javamexico.micronaut.web;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class HttpMappingData {
  final Integer httpStatusCode;
  final FailureMapper<?> mapper;
}
