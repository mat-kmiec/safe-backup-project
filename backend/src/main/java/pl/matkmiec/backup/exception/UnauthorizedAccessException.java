package pl.matkmiec.backup.exception;

import org.springframework.http.HttpStatus;

/** Exception thrown when a user tries to access a resource without proper authorization.
 * Provides a specific error message for unauthorized access. */
public class UnauthorizedAccessException extends ApiException {

    /** Constructor for UnauthorizedAccessException. */
    public UnauthorizedAccessException() {
        super("Unauthorized access", HttpStatus.FORBIDDEN);
    }

    /** Constructor for UnauthorizedAccessException.
     * @param message The error message to be displayed. */
    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }




}
