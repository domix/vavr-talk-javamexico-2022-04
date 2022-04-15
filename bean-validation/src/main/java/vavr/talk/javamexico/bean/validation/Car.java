package vavr.talk.javamexico.bean.validation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Car {
  @NotNull
  String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  String licensePlate;

  @Min(2)
  int seatCount;
}
