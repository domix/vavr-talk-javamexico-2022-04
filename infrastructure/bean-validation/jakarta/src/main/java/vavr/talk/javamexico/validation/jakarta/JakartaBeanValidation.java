package vavr.talk.javamexico.validation.jakarta;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import lombok.Builder;
import lombok.NonNull;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

@Builder
public class JakartaBeanValidation implements BeanValidator<ConstraintViolation<?>> {

  public static JakartaBeanValidation ofDefault() {
    return JakartaBeanValidation.builder().build();
  }

  @Builder.Default
  private final jakarta.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Nonnull
  @Override
  public <T> Set<ConstraintViolation<?>> validate(@Nonnull T object, @Nullable Class<?>... groups) {
    return Collections.unmodifiableSet(validator.validate(object, groups));
  }

  @Override
  public String getMessageCode(@NonNull ConstraintViolation<?> constraintViolation) {
    return constraintViolation.getMessageTemplate();
  }

  @Override
  public String getLocalizedMessage(@NonNull ConstraintViolation<?> constraintViolation) {
    return constraintViolation.getMessage();
  }

  @Override
  public String getPropertyPath(@NonNull ConstraintViolation<?> constraintViolation) {
    return constraintViolation.getPropertyPath().toString();
  }
}

