package vavr.talk.javamexico.validation.javax;

import lombok.Builder;
import lombok.NonNull;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

@Builder
public class JavaxBeanValidation implements BeanValidator<ConstraintViolation<?>> {

  public static JavaxBeanValidation ofDefaults() {
    return JavaxBeanValidation.builder().build();
  }

  @Builder.Default
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

