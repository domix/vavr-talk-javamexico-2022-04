package vavr.talk.javamexico.validation;

import org.assertj.core.api.Condition;
import vavr.talk.javamexico.Failure;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationApiTCK {

  public static boolean validate(BeanValidator<?> beanValidator, Object underValidation, int errorsFound) {
    final var validationResult = beanValidator.validateBean(underValidation);

    assertThat(validationResult).isNotNull();

    if (errorsFound == 0) {
      assertThat(validationResult.isRight()).isTrue();
      assertThat(validationResult.get()).isNotNull();
      return true;
    }

    assertThat(validationResult.isLeft()).isTrue();
    final var failure = validationResult.getLeft();

    assertThat(failure).is(noThrowableCause);
    assertThat(failure.getCode()).isNotEmpty();

    assertThat(failure.getReason()).isNotEmpty();

    final var i18nData = failure.getI18nData();
    assertThat(i18nData).isNotNull();
    assertThat(i18nData.getCode()).isNotEmpty();
    assertThat(i18nData.getDefaultMessage()).isNotEmpty();

    final var details = failure.getDetails();
    assertThat(details).isNotEmpty();
    assertThat(details).hasSize(errorsFound);

    details.forEach(detail -> {
      assertThat(detail).isNotNull();
      assertThat(detail.getCodeMessage()).isNotEmpty();
      assertThat(detail.getLocalizedMessage()).isNotEmpty();
      assertThat(detail.getPath()).isNotEmpty();
      assertThat(detail.getType()).is(validationErrorType);
    });

    return true;
  }

  final static Condition<Failure> noThrowableCause = new Condition<>(failure -> failure.getCause().toJavaOptional().isEmpty(), "has no cause");
  final static Condition<Failure.ErrorType> validationErrorType = new Condition<>(errorType -> Objects.equals(Failure.ErrorType.VALIDATION, errorType), "is validation type");
}
