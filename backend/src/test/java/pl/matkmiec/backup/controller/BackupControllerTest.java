package pl.matkmiec.backup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.matkmiec.backup.dto.BackupListDto;
import pl.matkmiec.backup.dto.BackupResponseDto;
import pl.matkmiec.backup.dto.BackupUploadDto;
import pl.matkmiec.backup.entity.BackupType;
import pl.matkmiec.backup.exception.BackupNotFoundException;
import pl.matkmiec.backup.exception.UnauthorizedAccessException;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.service.BackupService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.context.annotation.Import;
import pl.matkmiec.backup.config.SecurityConfig;
import pl.matkmiec.backup.security.JwtAuthenticationFilter;
import pl.matkmiec.backup.security.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(BackupController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("BackupController Test")
public class BackupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackupService backupService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String TEST_USERNAME = "testuser";
    private final UUID TEST_BACKUP_ID = UUID.randomUUID();

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("uploadBackup_ShouldReturnCreated_WhenBackupIsUploaded")
    void uploadBackup_ShouldReturnCreated_WhenBackupIsUploaded() throws Exception {
        BackupUploadDto dto = new BackupUploadDto();
        dto.setType(BackupType.SMS);
        dto.setPayload("{\"messages\": []}");

        doNothing().when(backupService).saveBackup(any(BackupUploadDto.class));

        mockMvc.perform(post("/api/v1/backups/upload")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Backup created successfully"));

        verify(backupService).saveBackup(any(BackupUploadDto.class));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("uploadBackup_ShouldReturnNotFound_WhenUserDoesNotExist")
    void uploadBackup_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        BackupUploadDto dto = new BackupUploadDto();
        dto.setType(BackupType.SMS);
        dto.setPayload("{\"messages\": []}");

        doThrow(new UserNotFoundException(TEST_USERNAME)).when(backupService).saveBackup(any());

        mockMvc.perform(post("/api/v1/backups/upload")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(backupService).saveBackup(any(BackupUploadDto.class));
    }

    @Test
    @DisplayName("uploadBackup_ShouldReturnUnauthorized_WhenNotAuthenticated")
    void uploadBackup_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        BackupUploadDto dto = new BackupUploadDto();
        dto.setType(BackupType.SMS);
        dto.setPayload("{\"messages\": []}");

        mockMvc.perform(post("/api/v1/backups/upload")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(backupService, never()).saveBackup(any());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("uploadBackup_ShouldReturnBadRequest_WhenValidationFails")
    void uploadBackup_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        BackupUploadDto dto = new BackupUploadDto();
        dto.setType(null);
        dto.setPayload("");

        mockMvc.perform(post("/api/v1/backups/upload")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(backupService, never()).saveBackup(any());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("deleteBackup_ShouldReturnOk_WhenBackupIsDeleted")
    void deleteBackup_ShouldReturnOk_WhenBackupIsDeleted() throws Exception {
        doNothing().when(backupService).deleteBackup(TEST_BACKUP_ID);

        mockMvc.perform(delete("/api/v1/backups/{backupId}", TEST_BACKUP_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Backup deleted successfully"));

        verify(backupService).deleteBackup(TEST_BACKUP_ID);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("deleteBackup_ShouldReturnNotFound_WhenBackupDoesNotExist")
    void deleteBackup_ShouldReturnNotFound_WhenBackupDoesNotExist() throws Exception {
        doThrow(new BackupNotFoundException(TEST_BACKUP_ID)).when(backupService).deleteBackup(TEST_BACKUP_ID);

        mockMvc.perform(delete("/api/v1/backups/{backupId}", TEST_BACKUP_ID)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(backupService).deleteBackup(TEST_BACKUP_ID);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("deleteBackup_ShouldReturnForbidden_WhenUserIsNotAuthorized")
    void deleteBackup_ShouldReturnForbidden_WhenUserIsNotAuthorized() throws Exception {
        doThrow(new UnauthorizedAccessException("Cannot delete other user's backup")).when(backupService).deleteBackup(TEST_BACKUP_ID);

        mockMvc.perform(delete("/api/v1/backups/{backupId}", TEST_BACKUP_ID)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(backupService).deleteBackup(TEST_BACKUP_ID);
    }

    @Test
    @DisplayName("deleteBackup_ShouldReturnUnauthorized_WhenNotAuthenticated")
    void deleteBackup_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/backups/{backupId}", TEST_BACKUP_ID)
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(backupService, never()).deleteBackup(any());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("getAllBackups_ShouldReturnOk_WhenBackupsExist")
    void getAllBackups_ShouldReturnOk_WhenBackupsExist() throws Exception {
        BackupListDto backup1 = BackupListDto.builder()
                .id(UUID.randomUUID())
                .type(BackupType.SMS)
                .payloadSize(100)
                .createdAt(LocalDateTime.now())
                .build();
        BackupListDto backup2 = BackupListDto.builder()
                .id(UUID.randomUUID())
                .type(BackupType.CONTACTS)
                .payloadSize(200)
                .createdAt(LocalDateTime.now())
                .build();
        List<BackupListDto> backups = List.of(backup1, backup2);

        when(backupService.getAllBackups()).thenReturn(backups);

        mockMvc.perform(get("/api/v1/backups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(backupService).getAllBackups();
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("getAllBackups_ShouldReturnOk_WhenNoBackupsExist")
    void getAllBackups_ShouldReturnOk_WhenNoBackupsExist() throws Exception {
        when(backupService.getAllBackups()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/backups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(backupService).getAllBackups();
    }

    @Test
    @DisplayName("getAllBackups_ShouldReturnUnauthorized_WhenNotAuthenticated")
    void getAllBackups_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/backups"))
                .andExpect(status().isUnauthorized());

        verify(backupService, never()).getAllBackups();
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("getBackup_ShouldReturnOk_WhenBackupExists")
    void getBackup_ShouldReturnOk_WhenBackupExists() throws Exception {
        BackupResponseDto backup = BackupResponseDto.builder()
                .id(TEST_BACKUP_ID)
                .type(BackupType.SMS)
                .payload("{\"messages\": []}")
                .createdAt(LocalDateTime.now())
                .build();
        when(backupService.getBackup(TEST_BACKUP_ID)).thenReturn(backup);

        mockMvc.perform(get("/api/v1/backups/{backupId}", TEST_BACKUP_ID))
                .andExpect(status().isOk());

        verify(backupService).getBackup(TEST_BACKUP_ID);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("getBackup_ShouldReturnNotFound_WhenBackupDoesNotExist")
    void getBackup_ShouldReturnNotFound_WhenBackupDoesNotExist() throws Exception {
        when(backupService.getBackup(TEST_BACKUP_ID)).thenThrow(new BackupNotFoundException(TEST_BACKUP_ID));

        mockMvc.perform(get("/api/v1/backups/{backupId}", TEST_BACKUP_ID))
                .andExpect(status().isNotFound());

        verify(backupService).getBackup(TEST_BACKUP_ID);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    @DisplayName("getBackup_ShouldReturnForbidden_WhenUserIsNotAuthorized")
    void getBackup_ShouldReturnForbidden_WhenUserIsNotAuthorized() throws Exception {
        when(backupService.getBackup(TEST_BACKUP_ID)).thenThrow(new UnauthorizedAccessException("Cannot access other user's backup"));

        mockMvc.perform(get("/api/v1/backups/{backupId}", TEST_BACKUP_ID))
                .andExpect(status().isForbidden());

        verify(backupService).getBackup(TEST_BACKUP_ID);
    }

    @Test
    @DisplayName("getBackup_ShouldReturnUnauthorized_WhenNotAuthenticated")
    void getBackup_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/backups/{backupId}", TEST_BACKUP_ID))
                .andExpect(status().isUnauthorized());

        verify(backupService, never()).getBackup(any());
    }
}
