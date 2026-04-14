package pl.matkmiec.backup.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a user in the backup system.
 * Each user has a unique username and a hashed password.
 * The createdAt field stores the timestamp of when the user was created.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** The unique identifier of the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The username of the user. */
    @Column(unique = true, nullable = false, length = 255)
    private String username;

    /** The hashed password of the user. */
    @Column(nullable = false, length = 255, name = "password_hash")
    private String password;

    /** The timestamp of when the user was created. */
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
}
