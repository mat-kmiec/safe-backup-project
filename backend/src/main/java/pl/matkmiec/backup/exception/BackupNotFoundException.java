package pl.matkmiec.backup.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

/** Exception thrown when a backup is not found. */
public class BackupNotFoundException extends ApiException {

    /** Constructor for BackupNotFoundException. */
    public BackupNotFoundException() {
        super("Backup not found", HttpStatus.NOT_FOUND);
    }

    /** Constructor for BackupNotFoundException.
     * @param backupId The ID of the backup that was not found. */
    public BackupNotFoundException(UUID backupId) {
        super("Backup not found", HttpStatus.NOT_FOUND, "Backup ID: " + backupId);
    }
}
