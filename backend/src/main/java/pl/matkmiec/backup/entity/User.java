package pl.matkmiec.backup.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 255, name = "password_hash")
    private String password;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
}
