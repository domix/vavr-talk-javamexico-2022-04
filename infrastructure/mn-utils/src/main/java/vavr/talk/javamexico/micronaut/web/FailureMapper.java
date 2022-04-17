package vavr.talk.javamexico.micronaut.web;

import vavr.talk.javamexico.Failure;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface FailureMapper<T> {
  @Nonnull
  T map(@Nonnull Failure failure);
}
