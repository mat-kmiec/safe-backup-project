package pl.matkmiec.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.matkmiec.backup.entity.BackupType;

import java.time.LocalDateTime;
import java.util.UUID;

/** DTO representing a backup response. */
@Data
@Builder
@AllArgsConstructor
public class BackupResponseDto {

    /** The unique identifier of the backup. */
    private UUID id;

    /** The type of the backup. */
    private BackupType type;

    /** The payload of the backup. */
    private String payload;

    /** The timestamp of when the backup was created. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
