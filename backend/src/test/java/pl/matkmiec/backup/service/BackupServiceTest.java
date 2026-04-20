package pl.matkmiec.backup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.matkmiec.backup.dto.BackupListDto;
import pl.matkmiec.backup.dto.BackupResponseDto;
import pl.matkmiec.backup.dto.BackupUploadDto;
import pl.matkmiec.backup.entity.BackupType;
import pl.matkmiec.backup.entity.Backups;
import pl.matkmiec.backup.entity.User;
import pl.matkmiec.backup.exception.BackupNotFoundException;
import pl.matkmiec.backup.exception.UnauthorizedAccessException;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.mapper.BackupMapper;
import pl.matkmiec.backup.repository.BackupRepository;
import pl.matkmiec.backup.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BackupService Test")
public class BackupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BackupRepository backupRepository;

    @Mock
    private BackupMapper backupMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BackupService backupService;

    private User testUser;
    private Backups testBackup;
    private BackupUploadDto backupUploadDto;
    private final String TEST_USERNAME = "testuser";
    private final UUID TEST_USER_ID = UUID.randomUUID();
    private final UUID TEST_BACKUP_ID = UUID.randomUUID();

    @BeforeEach
    void setUp(){
        testUser = User.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        testBackup = Backups.builder()
                .id(TEST_BACKUP_ID)
                .user(testUser)
                .type(BackupType.SMS)
                .payload("{\"messages\": []}")
                .createdAt(LocalDateTime.now())
                .build();

        backupUploadDto = new BackupUploadDto();
        backupUploadDto.setType(BackupType.SMS);
        backupUploadDto.setPayload("{\"messages\": []}");

        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("saveBackup_ShouldSaveBackup_WhenUserExists")
    void saveBackup_ShouldSaveBackup_WhenUserExists(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupMapper.toEntity(backupUploadDto)).thenReturn(testBackup);

        backupService.saveBackup(backupUploadDto);

        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(backupMapper).toEntity(backupUploadDto);
        verify(backupRepository).save(any(Backups.class));
    }

    @Test
    @DisplayName("saveBackup_ShouldThrowUserNotFoundException_WhenUserDoesNotExist")
    void saveBackup_ShouldThrowUserNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> backupService.saveBackup(backupUploadDto));
        verify(backupRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteBackup_ShouldDeleteBackup_WhenUserIsOwner")
    void deleteBackup_ShouldDeleteBackup_WhenUserIsOwner(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findById(TEST_BACKUP_ID)).thenReturn(Optional.of(testBackup));

        backupService.deleteBackup(TEST_BACKUP_ID);

        verify(backupRepository).delete(testBackup);
    }

    @Test
    @DisplayName("deleteBackup_ShouldThrowBackupNotFoundException_WhenBackupDoesNotExist")
    void deleteBackup_ShouldThrowBackupNotFoundException_WhenBackupDoesNotExist(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findById(TEST_BACKUP_ID)).thenReturn(Optional.empty());

        assertThrows(BackupNotFoundException.class, () -> backupService.deleteBackup(TEST_BACKUP_ID));
        verify(backupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteBackup_ShouldThrowUnauthorizedAccessException_WhenUserIsNotOwner")
    void deleteBackup_ShouldThrowUnauthorizedAccessException_WhenUserIsNotOwner(){
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("otheruser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        Backups otherUserBackup = Backups.builder()
                .id(TEST_BACKUP_ID)
                .user(otherUser)
                .type(BackupType.SMS)
                .payload("{\"messages\": []}")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findById(TEST_BACKUP_ID)).thenReturn(Optional.of(otherUserBackup));

        assertThrows(UnauthorizedAccessException.class, () -> backupService.deleteBackup(TEST_BACKUP_ID));
        verify(backupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getBackup_ShouldReturnBackup_WhenUserIsOwner")
    void getBackup_ShouldReturnBackup_WhenUserIsOwner(){
        BackupResponseDto expectedDto = BackupResponseDto.builder()
                .id(TEST_BACKUP_ID)
                .type(BackupType.SMS)
                .payload("{\"messages\": []}")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findById(TEST_BACKUP_ID)).thenReturn(Optional.of(testBackup));
        when(backupMapper.toResponseDto(testBackup)).thenReturn(expectedDto);

        BackupResponseDto result = backupService.getBackup(TEST_BACKUP_ID);

        assertEquals(expectedDto, result);
        verify(backupRepository).findById(TEST_BACKUP_ID);
        verify(backupMapper).toResponseDto(testBackup);
    }

    @Test
    @DisplayName("getBackup_ShouldThrowBackupNotFoundException_WhenBackupDoesNotExist")
    void getBackup_ShouldThrowBackupNotFoundException_WhenBackupDoesNotExist(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findById(TEST_BACKUP_ID)).thenReturn(Optional.empty());

        assertThrows(BackupNotFoundException.class, () -> backupService.getBackup(TEST_BACKUP_ID));
    }

    @Test
    @DisplayName("getBackup_ShouldThrowUnauthorizedAccessException_WhenUserIsNotOwner")
    void getBackup_ShouldThrowUnauthorizedAccessException_WhenUserIsNotOwner(){
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("otheruser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        Backups otherUserBackup = Backups.builder()
                .id(TEST_BACKUP_ID)
                .user(otherUser)
                .type(BackupType.SMS)
                .payload("{\"messages\": []}")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findById(TEST_BACKUP_ID)).thenReturn(Optional.of(otherUserBackup));

        assertThrows(UnauthorizedAccessException.class, () -> backupService.getBackup(TEST_BACKUP_ID));
    }

    @Test
    @DisplayName("getAllBackups_ShouldReturnAllBackups_WhenUserExists")
    void getAllBackups_ShouldReturnAllBackups_WhenUserExists(){
        Backups backup1 = Backups.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .type(BackupType.SMS)
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();

        Backups backup2 = Backups.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .type(BackupType.CONTACTS)
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();

        List<Backups> backups = List.of(backup1, backup2);
        BackupListDto backupDto1 = BackupListDto.builder()
                .id(backup1.getId())
                .type(BackupType.SMS)
                .payloadSize(2)
                .createdAt(LocalDateTime.now())
                .build();
        BackupListDto backupDto2 = BackupListDto.builder()
                .id(backup2.getId())
                .type(BackupType.CONTACTS)
                .payloadSize(2)
                .createdAt(LocalDateTime.now())
                .build();
        List<BackupListDto> backupDtos = List.of(backupDto1, backupDto2);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findAllByUserId(TEST_USER_ID)).thenReturn(backups);
        when(backupMapper.toListDto(backup1)).thenReturn(backupDtos.get(0));
        when(backupMapper.toListDto(backup2)).thenReturn(backupDtos.get(1));

        List<BackupListDto> result = backupService.getAllBackups();

        assertEquals(2, result.size());
        verify(backupRepository).findAllByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("getAllBackups_ShouldReturnEmptyList_WhenUserHasNoBackups")
    void getAllBackups_ShouldReturnEmptyList_WhenUserHasNoBackups(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(backupRepository.findAllByUserId(TEST_USER_ID)).thenReturn(List.of());

        List<BackupListDto> result = backupService.getAllBackups();

        assertEquals(0, result.size());
        verify(backupRepository).findAllByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("getAllBackups_ShouldThrowUserNotFoundException_WhenUserDoesNotExist")
    void getAllBackups_ShouldThrowUserNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> backupService.getAllBackups());
        verify(backupRepository, never()).findAllByUserId(any());
    }
}



