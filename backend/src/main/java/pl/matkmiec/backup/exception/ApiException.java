package pl.matkmiec.backup.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Base class for all API exceptions.
 * Provides a common structure for API exceptions.*/
@Getter
public abstract class ApiException extends RuntimeException {

    /** Http status code for the exception. */
    private final HttpStatus httpStatus;

    /** Additional details about the exception. */
    private final String details;

    /** Constructor for ApiException.
     * @param message The exception message.
     * @param httpStatus The HTTP status code for the exception.*/
    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.details = null;
    }

    /** Constructor for ApiException.
     * @param message The exception message.
     * @param httpStatus The HTTP status code for the exception.
     * @param details Additional details about the exception.*/
    public ApiException(String message, HttpStatus httpStatus, String details) {
        super(message);
        this.httpStatus = httpStatus;
        this.details = details;
    }

}
