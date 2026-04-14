package pl.matkmiec.backup.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/** Service for generating JWT tokens for user authentication. */
@Service
public class JwtService {

    private static final String SECRET = "secret";
    /** The secret key used for JWT signing and verification. */
    private static SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

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
}
