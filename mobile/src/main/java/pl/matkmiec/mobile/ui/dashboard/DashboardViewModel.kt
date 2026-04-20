package pl.matkmiec.mobile.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.matkmiec.mobile.api.BackupListDto
import pl.matkmiec.mobile.api.BackupUploadDto
import pl.matkmiec.mobile.api.RetrofitClient

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val backups: List<BackupListDto>) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState

    init {
        fetchBackups()
    }

    fun fetchBackups() {
        _uiState.value = DashboardState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.backupApi.getAllBackups()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _uiState.value = DashboardState.Success(list)
                } else {
                    _uiState.value = DashboardState.Error("Failed to fetch backups: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error(e.message ?: "Network error")
            }
        }
    }

    fun deleteBackup(backupId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.backupApi.deleteBackup(backupId)
                if (response.isSuccessful) {
                    // Update state locally or re-fetch
                    fetchBackups()
                } else {
                    // Could dispatch a one-time error event here, but for simplicity we ignore or log logic
                    fetchBackups() // Will refresh the list anyway
                }
            } catch (e: Exception) {
                // handle error silently
            }
        }
    }

    fun createBackup(type: String) {
        viewModelScope.launch {
            try {
                val fakePayload = "This is a beautiful fake generated payload for $type backup at ${System.currentTimeMillis()}"
                val request = BackupUploadDto(type = type, payload = fakePayload)
                val response = RetrofitClient.backupApi.uploadBackup(request)
                if (response.isSuccessful) {
                    fetchBackups()
                }
            } catch (e: Exception) {
                // ignore or handle error
            }
        }
    }
}

