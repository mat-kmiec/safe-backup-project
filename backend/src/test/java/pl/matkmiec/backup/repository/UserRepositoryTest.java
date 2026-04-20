package pl.matkmiec.backup.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.matkmiec.backup.entity.BackupType;
import pl.matkmiec.backup.entity.Backups;
import pl.matkmiec.backup.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByUsername_ShouldReturnUser_WhenUserExists")
    void findByUsername_ShouldReturnUser_WhenUserExists(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(user);

        Optional<User> result = userRepository.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist")
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist(){
        Optional<User> result = userRepository.findByUsername("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("existsByUsername_ShouldReturnTrue_WhenUserExists")
    void existsByUsername_ShouldReturnTrue_WhenUserExists(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(user);

        boolean result = userRepository.existsByUsername("testuser");

        assertTrue(result);
    }

    @Test
    @DisplayName("existsByUsername_ShouldReturnFalse_WhenUserDoesNotExist")
    void existsByUsername_ShouldReturnFalse_WhenUserDoesNotExist(){
        boolean result = userRepository.existsByUsername("nonexistent");

        assertFalse(result);
    }

    @Test
    @DisplayName("save_ShouldPersistUser_WhenUserIsValid")
    void save_ShouldPersistUser_WhenUserIsValid(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
    }

    @Test
    @DisplayName("findById_ShouldReturnUser_WhenUserExists")
    void findById_ShouldReturnUser_WhenUserExists(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        Optional<User> result = userRepository.findById(persistedUser.getId());

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("findById_ShouldReturnEmpty_WhenUserDoesNotExist")
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist(){
        Optional<User> result = userRepository.findById(UUID.randomUUID());

        assertFalse(result.isPresent());
    }
}

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BackupRepository Test")
class BackupRepositoryTest {

    @Autowired
    private BackupRepository backupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findAllByUserId_ShouldReturnAllBackups_WhenBackupsExist")
    void findAllByUserId_ShouldReturnAllBackups_WhenBackupsExist(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        Backups backup1 = Backups.builder()
                .user(persistedUser)
                .type(BackupType.SMS)
                .payload("{\"messages\": []}")
                .createdAt(LocalDateTime.now())
                .build();

        Backups backup2 = Backups.builder()
                .user(persistedUser)
                .type(BackupType.CONTACTS)
                .payload("{\"contacts\": []}")
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        entityManager.persistAndFlush(backup1);
        entityManager.persistAndFlush(backup2);

        List<Backups> result = backupRepository.findAllByUserId(persistedUser.getId());

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findAllByUserId_ShouldReturnEmptyList_WhenNoBackupsExist")
    void findAllByUserId_ShouldReturnEmptyList_WhenNoBackupsExist(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        List<Backups> result = backupRepository.findAllByUserId(persistedUser.getId());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("findAllByUserId_ShouldReturnBackupsOrderedByCreatedAtDesc")
    void findAllByUserId_ShouldReturnBackupsOrderedByCreatedAtDesc(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        LocalDateTime now = LocalDateTime.now();
        Backups backup1 = Backups.builder()
                .user(persistedUser)
                .type(BackupType.SMS)
                .payload("{}")
                .createdAt(now.minusHours(2))
                .build();

        Backups backup2 = Backups.builder()
                .user(persistedUser)
                .type(BackupType.CONTACTS)
                .payload("{}")
                .createdAt(now)
                .build();

        entityManager.persistAndFlush(backup1);
        entityManager.persistAndFlush(backup2);

        List<Backups> result = backupRepository.findAllByUserId(persistedUser.getId());

        assertEquals(2, result.size());
        assertEquals(backup2.getId(), result.get(0).getId());
        assertEquals(backup1.getId(), result.get(1).getId());
    }

    @Test
    @DisplayName("save_ShouldPersistBackup_WhenBackupIsValid")
    void save_ShouldPersistBackup_WhenBackupIsValid(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        Backups backup = Backups.builder()
                .user(persistedUser)
                .type(BackupType.SMS)
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();

        Backups savedBackup = backupRepository.save(backup);

        assertNotNull(savedBackup.getId());
        assertEquals(persistedUser.getId(), savedBackup.getUser().getId());
    }

    @Test
    @DisplayName("findById_ShouldReturnBackup_WhenBackupExists")
    void findById_ShouldReturnBackup_WhenBackupExists(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        Backups backup = Backups.builder()
                .user(persistedUser)
                .type(BackupType.SMS)
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();
        Backups persistedBackup = entityManager.persistAndFlush(backup);

        var result = backupRepository.findById(persistedBackup.getId());

        assertTrue(result.isPresent());
        assertEquals(BackupType.SMS, result.get().getType());
    }

    @Test
    @DisplayName("findById_ShouldReturnEmpty_WhenBackupDoesNotExist")
    void findById_ShouldReturnEmpty_WhenBackupDoesNotExist(){
        var result = backupRepository.findById(UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete_ShouldRemoveBackup_WhenBackupExists")
    void delete_ShouldRemoveBackup_WhenBackupExists(){
        User user = User.builder()
                .username("testuser")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        User persistedUser = entityManager.persistAndFlush(user);

        Backups backup = Backups.builder()
                .user(persistedUser)
                .type(BackupType.SMS)
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();
        Backups persistedBackup = entityManager.persistAndFlush(backup);

        backupRepository.delete(persistedBackup);
        entityManager.flush();

        var result = backupRepository.findById(persistedBackup.getId());

        assertFalse(result.isPresent());
    }
}
