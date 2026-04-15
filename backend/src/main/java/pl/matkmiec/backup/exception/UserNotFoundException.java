package pl.matkmiec.backup.exception;

import org.springframework.http.HttpStatus;

/** Exception thrown when a user is not found.
 * Provides a specific error message for user not found.*/
public class UserNotFoundException extends ApiException {

    /** Constructor for UserNotFoundException.*/
    public UserNotFoundException(){
        super("User not found", HttpStatus.NOT_FOUND);
    }

    /** Constructor for UserNotFoundException.
     * @param username The username of the user that was not found.*/
    public UserNotFoundException(String username){
        super("User not found", HttpStatus.NOT_FOUND, "Username: " + username);
    }
}
