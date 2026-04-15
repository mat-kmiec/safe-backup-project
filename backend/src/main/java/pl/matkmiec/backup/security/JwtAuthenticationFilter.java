package pl.matkmiec.backup.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/** Filter for authenticating users based on JWT tokens. */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** The service used for JWT token generation and verification. */
    private final JwtService jwtService;

    /** Extracts the username from the JWT token and sets it as the authentication principal. */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /* Check if the request contains an Authorization header */
        String authHeader = request.getHeader("Authorization");

        /* If the Authorization header is not present or does not start with "Bearer ",
        do nothing and let the request proceed without authentication */
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        /* Extract the token from the Authorization header */
        String token = authHeader.substring(7);

        /* Verify the token and set the username as the authentication principal */
        try{
            String username = jwtService.extractUsername(token);
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }catch (Exception e){
            // Invalid token, do nothing and let the request proceed without authentication
        }

        filterChain.doFilter(request, response);
    }
}
