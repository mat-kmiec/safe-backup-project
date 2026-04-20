package pl.matkmiec.mobile.api

import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody

interface BackupApiService {
    @GET("api/v1/backups")
    suspend fun getAllBackups(): Response<List<BackupListDto>>

    @GET("api/v1/backups/{backupId}")
    suspend fun getBackup(@Path("backupId") backupId: String): Response<BackupResponseDto>

    @POST("api/v1/backups/upload")
    suspend fun uploadBackup(@Body request: BackupUploadDto): Response<ResponseBody>

    @DELETE("api/v1/backups/{backupId}")
    suspend fun deleteBackup(@Path("backupId") backupId: String): Response<ResponseBody>
}

data class BackupListDto(
    val id: String,
    val type: String,
    val payloadSize: Int,
    val createdAt: String
)

data class BackupResponseDto(
    val id: String,
    val type: String,
    val payload: String,
    val createdAt: String
)

data class BackupUploadDto(
    val type: String,
    val payload: String
)

