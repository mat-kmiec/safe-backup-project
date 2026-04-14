package pl.matkmiec.backup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.matkmiec.backup.entity.Backups;

import java.util.UUID;

@Repository
public interface BackupRepository extends JpaRepository<Backups, UUID> {
}
