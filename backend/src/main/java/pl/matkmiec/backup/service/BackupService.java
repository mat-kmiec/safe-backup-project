package pl.matkmiec.backup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.matkmiec.backup.dto.BackupUploadDto;
import pl.matkmiec.backup.entity.Backups;
import pl.matkmiec.backup.entity.User;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.mapper.BackupMapper;
import pl.matkmiec.backup.repository.BackupRepository;
import pl.matkmiec.backup.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BackupService {


    private final UserRepository userRepository;
    private final BackupMapper backupMapper;
    private final BackupRepository backupRepository;

    @Transactional
    public void saveBackup(BackupUploadDto dto) throws UserNotFoundException {
        /** Get username from security context */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /** Get user by username */
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);


        Backups backup = backupMapper.toEntity(dto);
        backup.setUser(user);
        backup.setCreatedAt(LocalDateTime.now());
        backupRepository.save(backup);

    }
}
