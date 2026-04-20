package pl.matkmiec.backup.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.matkmiec.backup.dto.AuthRequestDto;
import pl.matkmiec.backup.entity.User;
import pl.matkmiec.backup.exception.PasswordMismatchException;
import pl.matkmiec.backup.exception.UserAlreadyExists;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.repository.UserRepository;
import pl.matkmiec.backup.security.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Test")
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "TestHardPassword123.!";
    private final String ENCODED_PASSWORD = "encodedPassword";

    @Test
    @DisplayName("register_ShouldSaveUser_WhenUsernameIsUnique")
    void register_ShouldSaveUser_WhenUsernameIsUnique(){
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn(ENCODED_PASSWORD);

        authService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(TEST_USERNAME, savedUser.getUsername());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("register_ShouldThrowException_WhenUserAlreadyExists")
    void register_ShouldThrowException_WhenUserAlreadyExists(){
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> authService.register(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login_ShouldReturnToken_WhenCredentialsAreCorrect")
    void login_ShouldReturnToken_WhenCredentialsAreCorrect(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .password(ENCODED_PASSWORD)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(TEST_USERNAME)).thenReturn("jwt_token_123");

        String token = authService.login(new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD));

        assertEquals("jwt_token_123", token);
        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(passwordEncoder).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("login_ShouldThrowUserNotFoundException_WhenUserDoesNotExist")
    void login_ShouldThrowUserNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD)));
        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("login_ShouldThrowPasswordMismatchException_WhenPasswordIsIncorrect")
    void login_ShouldThrowPasswordMismatchException_WhenPasswordIsIncorrect(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .password(ENCODED_PASSWORD)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(PasswordMismatchException.class, () -> authService.login(new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD)));
        verify(jwtService, never()).generateToken(any());
    }
}
