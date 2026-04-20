package pl.matkmiec.mobile.api

data class AuthRequestDto(
    val username: String,
    val password: String
)

data class AuthResponseDto(
    val token: String,
    val username: String,
    val type: String = "Bearer"
)

