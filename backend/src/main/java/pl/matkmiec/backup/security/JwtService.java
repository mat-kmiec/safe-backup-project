package pl.matkmiec.backup.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/** Service for generating JWT tokens for user authentication. */
@Service
@Slf4j
public class JwtService {


    /** The secret key used for signing JWT tokens. */
    private final SecretKey key;
    /** The expiration time for JWT tokens, in milliseconds. */
    private final long expiration;

    /* Constructor for JwtService. */
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
        log.info("JWT initialization with expiration" + expiration);
    }


    /** Generates a JWT token for the given user.
     * @param username The username for which the token is generated.
     * @return A JWT token string that can be used for authentication.
     * */
    public String generateToken(String username) {
        log.debug("Generating token for user: " + username);
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        try{
            String token = Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(key)
                    .compact();
            log.info("JWT token generated succesful for user: " + username);
            return token;
        }catch (Exception e){
            log.error("Error generating JWT token for user: " + username, e);
            throw e;
        }

    }

    /** Extracts the username from a JWT token.
     * @param token The JWT token string.
     * @return The username extracted from the token.
     * */
    public String extractUsername(String token){
        log.debug("Extracting username from JWT token");
        try {
            if (token == null || token.isEmpty()) {
                throw new JwtException("Invalid or expired JWT token");
            }
            String username = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            log.info("Username extracted from JWT token: " + username);
            return username;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid or expired JWT token: ", e.getMessage());
            throw new JwtException("Invalid or expired JWT token");
        } catch (JwtException e) {
            log.warn("Invalid or expired JWT token: ", e.getMessage());
            throw new JwtException("Invalid or expired JWT token");
        }

    }
}
