package pl.matkmiec.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.matkmiec.backup.entity.BackupType;

/** DTO representing a backup upload request. */
@Data
public class BackupUploadDto {

    /** The type of the backup. */
    @NotNull(message = "Type is required")
    private BackupType type;

    /** The payload of the backup. */
    @NotBlank(message = "Payload cannot be empty")
    private String payload;

}
