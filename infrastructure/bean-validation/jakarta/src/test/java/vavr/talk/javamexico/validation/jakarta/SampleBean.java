package vavr.talk.javamexico.validation.jakarta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SampleBean {
  @NotBlank
  String name;

  @NotNull
  String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  String licensePlate;

  @Min(2L)
  int seatCount;
}
