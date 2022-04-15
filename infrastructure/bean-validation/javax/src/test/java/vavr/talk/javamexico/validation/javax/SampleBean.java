package vavr.talk.javamexico.validation.javax;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
