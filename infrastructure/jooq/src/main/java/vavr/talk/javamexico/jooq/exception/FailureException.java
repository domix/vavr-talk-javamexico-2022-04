package vavr.talk.javamexico.jooq.exception;

import lombok.Getter;
import vavr.talk.javamexico.Failure;

/**
 * This exception wrap a Failure in case of need throw an exception but preserves the failure.
 */
public class FailureException extends RuntimeException {

    @Getter
    Failure failure;

    public FailureException(final Failure failure) {
        super("Failure: %s".formatted(failure));
        this.failure = failure;
    }

}
