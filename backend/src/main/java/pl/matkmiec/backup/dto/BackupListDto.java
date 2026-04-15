package pl.matkmiec.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.matkmiec.backup.entity.BackupType;

import java.time.LocalDateTime;
import java.util.UUID;

/** DTO representing a backup list response. */
@Data
@Builder
@AllArgsConstructor
public class BackupListDto {

    /** The unique identifier of the backup. */
    private UUID id;

    /** The type of the backup. */
    private BackupType type;

    /** The size of the backup payload in bytes. */
    private Integer payloadSize;

    /** The timestamp of when the backup was created. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
