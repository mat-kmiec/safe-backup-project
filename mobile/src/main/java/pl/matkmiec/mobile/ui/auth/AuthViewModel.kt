package pl.matkmiec.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import pl.matkmiec.mobile.api.AuthRequestDto
import pl.matkmiec.mobile.api.RetrofitClient
import pl.matkmiec.mobile.api.TokenManager

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun login(request: AuthRequestDto) {
        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        TokenManager.token = authResponse.token
                    }
                    _loginState.value = AuthState.Success("Logged in successfully!")
                } else {
                    _loginState.value = AuthState.Error(response.message() ?: "Login failed")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun register(request: AuthRequestDto) {
        _registerState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.register(request)
                if (response.isSuccessful) {
                    _registerState.value = AuthState.Success("Registered successfully! Please login.")
                } else {
                    val errorBody = response.errorBody()?.string()
                    var errorMessage = "Registration failed"
                    if (errorBody != null) {
                        try {
                            val json = JSONObject(errorBody)
                            if (json.has("details") && !json.isNull("details")) {
                                errorMessage = json.getString("details")
                            } else if (json.has("message") && !json.isNull("message")) {
                                errorMessage = json.getString("message")
                            }
                        } catch (e: Exception) {
                            errorMessage = errorBody
                        }
                    }
                    _registerState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetStates() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}
