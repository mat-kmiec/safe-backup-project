package pl.matkmiec.backup.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.matkmiec.backup.dto.AuthRequestDto;
import pl.matkmiec.backup.dto.AuthResponseDto;
import pl.matkmiec.backup.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    /** The service used for authentication-related operations. */
    private final AuthService authService;

    /** Register a new user.
     * @param authRequestDto The request body containing user information.
     * @return A response entity indicating the result of the registration process.
     * */
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided credentials.")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    @RateLimiter(name = "register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestDto authRequestDto) {
        log.info("Received registration request for user: {}", authRequestDto.getUsername());
        authService.register(authRequestDto);
        log.info("User registered successfully: {}", authRequestDto.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /** Login a user.
     * @param authRequestDto The request body containing user credentials.
     * @return A response entity containing the JWT token and user information if the login is successful.
     * */
    @Operation(summary = "Login a user", description = "Logs in a user with the provided credentials.")
    @RateLimiter(name = "login")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        log.info("Received login request for user: {}", authRequestDto.getUsername());
        String token = authService.login(authRequestDto);
        log.info("User logged in successfully: {}", authRequestDto.getUsername());
        return ResponseEntity.ok(AuthResponseDto.builder()
                .token(token)
                .username(authRequestDto.getUsername())
                .type("Bearer")
                .build());
    }
}
