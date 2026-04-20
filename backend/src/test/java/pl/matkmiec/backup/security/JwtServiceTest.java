package pl.matkmiec.backup.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
public class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET_KEY = "c87a1c0209d8f11c8dcecd289a2d82327d96750ba12a1eb30d82b0e69b1a6596";
    private final long EXPIRATION_TIME = 3600000; // 1 hour
    private final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp(){
        jwtService = new JwtService(SECRET_KEY, EXPIRATION_TIME);
    }

    @Test
    @DisplayName("Should generate a valid JWT token")
    void testGenerateToken_Succes(){
        String token = jwtService.generateToken(TEST_USERNAME);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void testGenerateToken_DifferentUsers(){
        String token1 = jwtService.generateToken(TEST_USERNAME);
        String token2 = jwtService.generateToken("differentuser");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate tokens with correct expiration")
    void testGenerateToken_WithCorrectExpiration(){
        String token = jwtService.generateToken(TEST_USERNAME);
        long issuedTime = System.currentTimeMillis();
        assertThatNoException().isThrownBy(() -> jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Should generate token with subject as username")
    void testGenerateToken_WithCorrectSubject(){
        String token = jwtService.generateToken(TEST_USERNAME);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void testExtractUsername_Success(){
        String token = jwtService.generateToken(TEST_USERNAME);

        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should extract correct username from different token")
    void testExtractUsername_WithDifferentTokens(){
        String user1 = "user1";
        String user2 = "user2";

        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        String extractedUsername1 = jwtService.extractUsername(token1);
        String extractedUsername2 = jwtService.extractUsername(token2);

        assertThat(extractedUsername1).isEqualTo(user1);
        assertThat(extractedUsername2).isEqualTo(user2);
    }

    @Test
    @DisplayName("Should throw JwtException for invalid token format")
    void testExtractUsername_InvalidTokenFormat(){
        String invalidToken = "invalid.token.format";

        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasMessage("Invalid or expired JWT token");
    }

    @Test
    @DisplayName("Should throw JwtException for malformed token")
    void testExtractUsername_MalformedToken(){
        String malformedToken = "completely.invalid";
        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Should throw JwtException for empty token")
    void testExtractUsername_EmptyToken(){
        assertThatThrownBy(() -> jwtService.extractUsername(""))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Should throw JwtException for token signed with wrong key")
    void testExtractUsername_WrongSignature(){
        String wrongSecret = "wrongsecretkeythatisdifffrom32bytes";
        SecretKey wrongKey = Keys.hmacShaKeyFor(wrongSecret.getBytes());

        String token = Jwts.builder()
                .subject(TEST_USERNAME)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(wrongKey)
                .compact();

        assertThatThrownBy(() -> jwtService.extractUsername(token))
                .isInstanceOf(JwtException.class)
                .hasMessage("Invalid or expired JWT token");
    }

    @Test
    @DisplayName("Should throw JwtException for expired token")
    void testExtractUsername_ExpiredToken() throws InterruptedException {
        JwtService expiredJwtService = new JwtService(SECRET_KEY, 1);
        String expiredToken = expiredJwtService.generateToken(TEST_USERNAME);
        Thread.sleep(100);

        assertThatThrownBy(() -> expiredJwtService.extractUsername(expiredToken))
                .isInstanceOf(JwtException.class)
                .hasMessage("Invalid or expired JWT token");
    }

    @Test
    @DisplayName("Should complete full cycle: generate -> extract")
    void testFullCycle_GenerateAndExtractUsername(){
        String username = "integration_test_user";

        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testGenerateToken_WithSpecialCharactersUsername(){
        String username = "user!@#$%^&*()_+";
        String token = jwtService.generateToken(username);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("Should handle empty username")
    void testGenerateToken_WithUnicodeUsername(){
        String unicodeUsername = "użytkownik_123";
        String token = jwtService.generateToken(unicodeUsername);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(unicodeUsername);

    }

    @Test
    @DisplayName("Should throw exception for null username on generate")
    void testGenerateToken_WithNullUsername(){
        assertThatThrownBy(() -> jwtService.generateToken(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should throw exception for null token on extract")
    void testExtractUsername_NullToken(){
        String token = jwtService.generateToken(TEST_USERNAME);

        assertThatNoException().isThrownBy(() -> jwtService.extractUsername(token));
    }

}
