package vavr.talk.javamexico.investing;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class InvestingUser {
  Long id;
  @NotBlank
  String firstName;
  @NotBlank
  String lastName;
  @Email
  String email;
  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;

}
