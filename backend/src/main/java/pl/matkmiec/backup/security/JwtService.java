package pl.matkmiec.backup.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/** Service for generating JWT tokens for user authentication. */
@Service
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
    }


    /** Generates a JWT token for the given user.
     * @param username The username for which the token is generated.
     * @return A JWT token string that can be used for authentication.
     * */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(key)
                .compact();
    }

    /** Extracts the username from a JWT token.
     * @param token The JWT token string.
     * @return The username extracted from the token.
     * */
    public String extractUsername(String token){
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired JWT token");
        }

    }
}
