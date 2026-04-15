package pl.matkmiec.backup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.matkmiec.backup.dto.BackupListDto;
import pl.matkmiec.backup.dto.BackupResponseDto;
import pl.matkmiec.backup.dto.BackupUploadDto;
import pl.matkmiec.backup.service.BackupService;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/backups")
@RequiredArgsConstructor
@Tag(name = "Backups", description = "Endpoints to manage backups")
public class BackupController {


    private final BackupService backupService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a backup", description = "Uploads a new backup.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Backup created successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required (bad token)"),
            @ApiResponse(responseCode = "403", description = "Access denied (user not authorized)"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<String> uploadBackup(@Valid @RequestBody BackupUploadDto backupUploadDto)  {
        backupService.saveBackup(backupUploadDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Backup created successfully");

    }

    @DeleteMapping("/{backupId}")
    @Operation(summary = "Delete a backup", description = "Deletes a backup with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Backup deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Backup not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required (bad token)"),
            @ApiResponse(responseCode = "403", description = "Access denied (user not authorized)")

    })
    public ResponseEntity<String> deleteBackup(@PathVariable UUID backupId) {
        backupService.deleteBackup(backupId);
        return ResponseEntity.status(HttpStatus.OK).body("Backup deleted successfully");

    }

    @GetMapping
    @Operation(summary = "Get all backups", description = "Retrieves a list of all backups.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of backups retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required (bad token)")
    })
    public ResponseEntity<List<BackupListDto>> getAllBackups() {
        List<BackupListDto> backups = backupService.getAllBackups();
        return ResponseEntity.ok(backups);
    }

    @GetMapping("/{backupId}")
    @Operation(summary = "Get a backup by ID", description = "Retrieves a backup by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Backup retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Backup not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required (bad token)"),
            @ApiResponse(responseCode = "403", description = "Access denied (user not authorized)")
    })
    public ResponseEntity<BackupResponseDto> getBackup(@PathVariable UUID backupId) {
        BackupResponseDto backup = backupService.getBackup(backupId);
        return ResponseEntity.ok(backup);
    }
}
