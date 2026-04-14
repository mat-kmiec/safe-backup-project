package pl.matkmiec.backup.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/** * Entity representing a backup in the system.
 * Each backup is associated with a user and has a specific type (e.g., SMS, CONTACTS).
 * The payLoad field stores the backup data in JSON format.
 * The createdAt field stores the timestamp of when the backup was created.
 */
@Entity
@Table(name = "backups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Backups {
    /** The unique identifier of the backup. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    /** The user associated with the backup. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The type of the backup (e.g., SMS, CONTACTS). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BackupType type;


    /** The payload of the backup (e.g., SMS content, contact details). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payLoad;

    /** The timestamp of when the backup was created. */
    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
