package pl.matkmiec.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/** DTO representing a request for authentication. */
@Data
@AllArgsConstructor
public class AuthRequestDto {

    /** The username of the user. */
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscore and hyphen")
    private String username;

    /** The password of the user. */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max=128, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{6,128}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String password;
}
