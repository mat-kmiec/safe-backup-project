package pl.matkmiec.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** DTO representing a request for authentication. */
@Data
public class AuthRequestDto {

    /** The username of the user. */
    @NotBlank(message = "Username cannot be empty")
    private String username;

    /** The password of the user. */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
