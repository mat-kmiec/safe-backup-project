package pl.matkmiec.backup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.matkmiec.backup.dto.BackupListDto;
import pl.matkmiec.backup.dto.BackupResponseDto;
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

    /** Converts a Backups entity to a BackupResponseDto.
     * @param backup The Backups entity to convert.
     * @return The converted BackupResponseDto.*/
    BackupResponseDto toResponseDto(Backups backup);

    /** Converts a Backups entity to a BackupListDto.
     * @param backup The Backups entity to convert.
     * @return The converted BackupListDto.*/
    @Mapping(target = "payloadSize", expression = "java(backup.getPayload().length())")
    BackupListDto toListDto(Backups backup);
}
