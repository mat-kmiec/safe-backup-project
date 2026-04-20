package pl.matkmiec.backup.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.matkmiec.backup.dto.AuthRequestDto;
import pl.matkmiec.backup.exception.PasswordMismatchException;
import pl.matkmiec.backup.exception.UserAlreadyExists;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.service.AuthService;

import org.springframework.context.annotation.Import;
import pl.matkmiec.backup.config.SecurityConfig;
import pl.matkmiec.backup.security.JwtAuthenticationFilter;
import pl.matkmiec.backup.security.JwtService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("AuthController Test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "TestHardPassword123.!";

    @Test
    @DisplayName("register_ShouldReturnCreated_WhenRegistrationIsSuccessful")
    void register_ShouldReturnCreated_WhenRegistrationIsSuccessful() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        doNothing().when(authService).register(dto);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

        verify(authService).register(dto);
    }

    @Test
    @DisplayName("register_ShouldReturnConflict_WhenUserAlreadyExists")
    void register_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        doThrow(new UserAlreadyExists()).when(authService).register(dto);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

        verify(authService).register(dto);
    }

    @Test
    @DisplayName("register_ShouldReturnBadRequest_WhenValidationFails")
    void register_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        AuthRequestDto dto = new AuthRequestDto("ab", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    @DisplayName("register_ShouldReturnBadRequest_WhenPasswordIsTooWeak")
    void register_ShouldReturnBadRequest_WhenPasswordIsTooWeak() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, "weak");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    @DisplayName("login_ShouldReturnOkWithToken_WhenCredentialsAreCorrect")
    void login_ShouldReturnOkWithToken_WhenCredentialsAreCorrect() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        String token = "jwt_token_123";
        when(authService.login(dto)).thenReturn(token);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(authService).login(dto);
    }

    @Test
    @DisplayName("login_ShouldReturnNotFound_WhenUserDoesNotExist")
    void login_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        when(authService.login(dto)).thenThrow(new UserNotFoundException(TEST_USERNAME));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(authService).login(dto);
    }

    @Test
    @DisplayName("login_ShouldReturnUnauthorized_WhenPasswordIsIncorrect")
    void login_ShouldReturnUnauthorized_WhenPasswordIsIncorrect() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        when(authService.login(dto)).thenThrow(new PasswordMismatchException());

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(dto);
    }

    @Test
    @DisplayName("login_ShouldReturnBadRequest_WhenValidationFails")
    void login_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        AuthRequestDto dto = new AuthRequestDto(TEST_USERNAME, "");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }
}

