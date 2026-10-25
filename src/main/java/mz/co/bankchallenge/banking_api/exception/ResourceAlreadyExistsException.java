package mz.co.bankchallenge.banking_api.exception;

/** Indicates that a requested resource conflicts with an existing resource. */
public class ResourceAlreadyExistsException extends RuntimeException {

    /** @param message description of the conflict */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
