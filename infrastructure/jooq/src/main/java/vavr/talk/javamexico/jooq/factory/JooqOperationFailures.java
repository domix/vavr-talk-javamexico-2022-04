package vavr.talk.javamexico.jooq.factory;

import vavr.talk.javamexico.Failure;

/**
 * Simple helper class to handle the potential failure cases
 */
public record JooqOperationFailures(String domainName) {

    public static final String BASE_FAILURE_CODE = "%s.failure.%s";

    public static JooqOperationFailures create(final String domainName) {
        return new JooqOperationFailures(domainName);
    }

    /**
     * Creates an instance of {@link Failure}
     *
     * @param throwable {@link Throwable}
     * @param operation {@link String} a simple identifier of the operation performed
     * @return {@link Failure}
     */
    public Failure createFailure(final Throwable throwable,
                                 final String operation) {
        return Failure.of(throwable, BASE_FAILURE_CODE.formatted(domainName, operation));
    }

    /**
     * Creates an instance of {@link Failure}
     *
     * @param operation   {@link String} a simple identifier of the operation performed
     * @param failureCase {@link String} a simple identifier of the case of failure
     * @param reason      a simple {@link String}
     * @return {@link Failure}
     */
    public Failure createFailure(final String operation,
                                 final String failureCase,
                                 final String reason) {
        final var baseCode = BASE_FAILURE_CODE.formatted(domainName, operation);
        return Failure.builder()
            .code("%s.%s".formatted(baseCode, failureCase))
            .reason(reason)
            .build();
    }

}

