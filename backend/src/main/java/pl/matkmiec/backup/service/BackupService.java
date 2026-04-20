package pl.matkmiec.backup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.matkmiec.backup.dto.BackupListDto;
import pl.matkmiec.backup.dto.BackupResponseDto;
import pl.matkmiec.backup.dto.BackupUploadDto;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {


    /** The repository used for user data access. */
    private final UserRepository userRepository;

    /** The mapper used for converting between DTOs and entities. */
    private final BackupMapper backupMapper;

    /** The repository used for backup data access. */
    private final BackupRepository backupRepository;

    /** Save backup to database
     * @param dto Backup data transfer object
     * @throws UserNotFoundException If the user is not found.*/
    @Transactional
    public void saveBackup(BackupUploadDto dto){
        /** Get username from security context */
        log.info("Saving backup for user: " + SecurityContextHolder.getContext().getAuthentication().getName());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /** Get user by username */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for username: " + username);
                    return new UserNotFoundException(username);
                });

        /** Save backup */
        Backups backup = backupMapper.toEntity(dto);
        backup.setUser(user);
        backup.setCreatedAt(LocalDateTime.now());
        backupRepository.save(backup);
        log.info("Backup saved successfully: " + backup.getId());

    }

    /** Delete backup from database
     * @param backupId Backup id
     * @throws BackupNotFoundException If the backup is not found.
     * @throws UnauthorizedAccessException If the user is not authorized to delete the backup.*/
    @Transactional
    public void deleteBackup(UUID backupId){
        log.info("Deleting backup with id: " + backupId);
        /** Get username from security context */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /** Get user by username */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for username: " + username);
                    return new UserNotFoundException(username);
                });

        /** Get backup by id */
        Backups backup = backupRepository.findById(backupId)
                .orElseThrow(() -> {
                    log.error("Backup not found for id: " + backupId);
                    return new BackupNotFoundException(backupId);
                });

        /** Check if user is authorized to access backup */
        if(!backup.getUser().getId().equals(user.getId())){
            log.error("User not authorized to delete backup with id: " + backupId);
            throw new UnauthorizedAccessException("Cannot delete other user's backup");
        }

        backupRepository.delete(backup);
        log.info("Backup deleted successfully: " + backupId);
    }

    /** Get backup from database
     * @param backupId Backup id
     * @return Backup data transfer object
     * @throws BackupNotFoundException If the backup is not found.
     * @throws UnauthorizedAccessException If the user is not authorized to access the backup.*/
    @Transactional(readOnly = true)
    public BackupResponseDto getBackup(UUID backupId){
        log.info("Getting backup with id: " + backupId);
        /** Get username from security context */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /** Get user by username */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for username: " + username);
                    return new UserNotFoundException(username);
                });

        /** Get backup by id */
        Backups backup = backupRepository.findById(backupId)
                .orElseThrow(() -> {
                    log.error("Backup not found for id: " + backupId);
                    return new BackupNotFoundException(backupId);
                });

        /** Check if user is authorized to access backup */
        if(!backup.getUser().getId().equals(user.getId())){
            log.error("User not authorized to access backup with id: " + backupId);
            throw new UnauthorizedAccessException("Cannot access other user's backup");
        }

        /** Convert backup to DTO */
        log.info("Found backup with id: " + backupId);
        return backupMapper.toResponseDto(backup);
    }

    /** Get all backups from database
     * @return List of backup data transfer objects
     * @throws UserNotFoundException If the user is not found.*/
    @Transactional(readOnly = true)
    public List<BackupListDto> getAllBackups(){
        log.info("Getting all backups for user: " + SecurityContextHolder.getContext().getAuthentication().getName());
        /** Get username from security context */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /** Get user by username */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for username: " + username);
                    return new UserNotFoundException(username);
                });

        /** Get all backups for user */
        List<Backups> backups = backupRepository.findAllByUserId(user.getId());

        /** Convert backups to list of DTOs */
        log.info("Found " + backups.size() + " backups for user: " + username);
        return backups.stream().map(backupMapper::toListDto).toList();
    }

}
