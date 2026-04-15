package pl.matkmiec.backup.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.matkmiec.backup.dto.AuthRequestDto;
import pl.matkmiec.backup.exception.PasswordMismatchException;
import pl.matkmiec.backup.exception.UserAlreadyExists;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    /** The service used for authentication-related operations. */
    private final AuthService authService;

    /** Register a new user.
     * @param authRequestDto The request body containing user information.
     * @return A response entity indicating the result of the registration process.
     * @throws UserAlreadyExists If the user already exists.
     * */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestDto authRequestDto) throws UserAlreadyExists {
        authService.register(authRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /** Login a user.
     * @param authRequestDto The request body containing user credentials.
     * @return A JWT token representing the user's authentication.
     * @throws UserNotFoundException If the user is not found.
     * @throws PasswordMismatchException If the password is incorrect.
     * */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequestDto authRequestDto) throws UserNotFoundException, PasswordMismatchException {
        String token = authService.login(authRequestDto);
        return ResponseEntity.ok(token);
    }
}
