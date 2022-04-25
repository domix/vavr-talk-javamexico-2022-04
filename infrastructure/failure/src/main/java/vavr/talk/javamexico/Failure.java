package vavr.talk.javamexico;

import io.vavr.control.Option;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import javax.annotation.Nonnull;
import java.util.List;

@ToString
@Value
@Builder
public class Failure {

  public static final String DEFAULT_CODE = "failure.code.default";
  public static final String DEFAULT_I18N_CODE = "failure.i18.default";

  @Nonnull
  @Builder.Default
  String code = DEFAULT_CODE;

  @Nonnull
  String reason;

  @Builder.Default
  List<Detail> details = List.of();

  @Nonnull
  @Builder.Default
  Option<Throwable> cause = Option.none();

  @Builder.Default
  I18nData i18nData = I18nData.instance;

  public enum ErrorType {
    UNEXPECTED, VALIDATION, BUSINESS
  }

  @ToString
  @Value
  @Builder
  public static class I18nData {
    /**
     * The code used for getting the message from MessageSource
     */
    @Nonnull
    @Builder.Default
    String code = DEFAULT_I18N_CODE;
    /**
     * Default message in case not found in MessageSource
     */
    @Nonnull
    @Builder.Default
    String defaultMessage = "";

    public static I18nData instance = Failure.I18nData.builder().build();
  }

  @ToString
  @Value
  @Builder
  public static class Detail {
    /**
     * An end user friendly message with the information about the error.
     */
    @Builder.Default
    String localizedMessage = "";
    /**
     * The key to load from the WebApi client the message if desired. It could be empty.
     */
    @Builder.Default
    String codeMessage = "";
    /**
     * The error type just to give more context
     */
    @Builder.Default
    ErrorType type = ErrorType.UNEXPECTED;
    /**
     * Useful to know what field of a given object had an error, useful for displaying in user interfaces if needed. It could be empty
     */
    @Builder.Default
    String path = "";
  }

  @Nonnull
  public static Failure of(
    final @Nonnull Throwable cause
  ) {
    return of(cause, DEFAULT_I18N_CODE, cause.getMessage());
  }

  @Nonnull
  public static Failure of(
    final @Nonnull Throwable cause,
    final @Nonnull String i18nCode,
    final @Nonnull String reason
  ) {
    final var i18nData = Failure.I18nData.builder()
      .defaultMessage(reason)
      .code(i18nCode)
      .build();
    return Failure.builder()
      .i18nData(i18nData)
      .code(i18nCode)
      .reason(reason)
      .cause(Option.of(cause))
      .build();
  }

  @Nonnull
  public static Failure of(
    final @Nonnull String i18nCode,
    final @Nonnull String reason
  ) {
    final var i18nData = Failure.I18nData.builder()
      .defaultMessage(reason)
      .code(i18nCode)
      .build();
    return Failure.builder()
      .i18nData(i18nData)
      .code(i18nCode)
      .reason(reason)
      .build();
  }

  @Nonnull
  public static Failure ofValidationErrors(final @Nonnull List<Detail> details) {
    return Failure.builder()
      .reason("Validation errors found")
      .details(details)
      .build();
  }

  @Nonnull
  public static Failure of(final @Nonnull Throwable throwable, final @Nonnull String failureCode) {
    return of(throwable, failureCode, DEFAULT_I18N_CODE);
  }

  @Nonnull
  public static Failure of(final @Nonnull Detail detail) {
    return ofValidationErrors(List.of(detail));
  }

  public static Failure of(String reason) {
    return of(DEFAULT_I18N_CODE, reason);
  }

  @Nonnull
  public static Failure.Detail ofValidationError(
    final @Nonnull String codeMessage,
    final @Nonnull String localizedMessage,
    final @Nonnull String path
  ) {
    return of(codeMessage, localizedMessage, path, ErrorType.VALIDATION);
  }

  @Nonnull
  public static Failure.Detail of(
    final @Nonnull String codeMessage,
    final @Nonnull String localizedMessage,
    final @Nonnull String path,
    final @Nonnull Failure.ErrorType type
  ) {
    return Failure.Detail.builder()
      .codeMessage(codeMessage)
      .localizedMessage(localizedMessage)
      .path(path)
      .type(type)
      .build();
  }
}

