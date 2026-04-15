package pl.matkmiec.backup.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.matkmiec.backup.dto.BackupUploadDto;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.service.BackupService;


@RestController
@RequestMapping("/api/v1/backups")
@RequiredArgsConstructor
@Tag(name = "Backups", description = "Endpoints to manage backups")
public class BackupController {


    private final BackupService backupService;

    @PostMapping("/upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Backup created successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required (bad token)"),
            @ApiResponse(responseCode = "403", description = "Access denied (user not authorized)"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<String> uploadBackup(@Valid @RequestBody BackupUploadDto backupUploadDto) throws UserNotFoundException {
        backupService.saveBackup(backupUploadDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Backup created successfully");

    }
}
