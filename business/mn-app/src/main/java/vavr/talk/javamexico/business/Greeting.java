package vavr.talk.javamexico.business;

import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nonnull;

@Builder
@Getter
public class Greeting {
  String message;

  @Nonnull
  public static Greeting of(@Nonnull String message) {
    return Greeting.builder().message(message).build();
  }
}
