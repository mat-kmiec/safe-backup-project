package pl.matkmiec.backup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** DTO representing a response for authentication. */
@Data
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    /* The JWT token used for authentication. */
    private String token;
    /* The username of the authenticated user. */
    private String username;
    /* The type of the token, e.g., Bearer. */
    private String type;
}
