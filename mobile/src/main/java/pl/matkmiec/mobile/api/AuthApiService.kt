package pl.matkmiec.mobile.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: AuthRequestDto): Response<ResponseBody>
}
