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
import pl.matkmiec.backup.exception.UserAlreadyExists;
import pl.matkmiec.backup.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    /** The service used for authentication-related operations. */
    private final AuthService authService;

    /** Registers a new user. */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestDto authRequestDto) throws UserAlreadyExists {
        authService.register(authRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /** Login a user. */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        String token = authService.login(authRequestDto);
        return ResponseEntity.ok(token);
    }
}
