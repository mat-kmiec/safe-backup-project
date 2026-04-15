package pl.matkmiec.backup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.matkmiec.backup.entity.Backups;

import java.util.List;
import java.util.UUID;

/** Repository for managing backups in the database. */
@Repository
public interface BackupRepository extends JpaRepository<Backups, UUID> {

    /** Finds all backups for a specific user. */
    @Query("SELECT b FROM Backups b WHERE b.user.id = :userId ORDER BY b.createdAt DESC ")
    List<Backups> findAllByUserId(@Param("userId") UUID id);
}
