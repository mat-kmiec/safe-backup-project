package pl.matkmiec.backup.exception;

import org.springframework.http.HttpStatus;

/** Exception thrown when a user already exists.
 * Provides a specific error message for user already exists. */
public class UserAlreadyExists extends ApiException{

    /** Constructor for UserAlreadyExists.*/
    public UserAlreadyExists(){
        super("User already exists", HttpStatus.CONFLICT);
    }

    /** Constructor for UserAlreadyExists.
     * @param username The username of the user that already exists.*/
    public UserAlreadyExists(String username){
        super("User already exists", HttpStatus.CONFLICT, "Username: " + username);
    }
}
