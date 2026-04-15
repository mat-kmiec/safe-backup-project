package pl.matkmiec.backup.exception;

import org.springframework.http.HttpStatus;

/** Exception thrown when the password provided by the user does not match the stored password.
 * Provides a specific error message for password mismatch.*/
public class PasswordMismatchException extends ApiException {

    /** Constructor for PasswordMismatchException.*/
    public PasswordMismatchException(){
        super("Password mismatch", HttpStatus.UNAUTHORIZED);
    }

    /** Constructor for PasswordMismatchException.
     * @param message The error message to be displayed.*/
    public PasswordMismatchException(String message){
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
