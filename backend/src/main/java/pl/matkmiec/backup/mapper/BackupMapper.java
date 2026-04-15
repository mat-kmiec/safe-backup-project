package pl.matkmiec.backup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.matkmiec.backup.dto.BackupUploadDto;
import pl.matkmiec.backup.entity.Backups;

/** Mapper for converting between BackupUploadDto and Backups. */
@Mapper(componentModel = "spring")
public interface BackupMapper {

    /** Converts a BackupUploadDto to a Backups entity.
     * @param backupUploadDto The BackupUploadDto to convert.
     * @return The converted Backups entity.*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Backups toEntity(BackupUploadDto backupUploadDto);
}
