package vavr.talk.javamexico;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class User {

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