package pl.matkmiec.mobile.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.matkmiec.mobile.api.BackupListDto
import pl.matkmiec.mobile.api.BackupUploadDto
import pl.matkmiec.mobile.api.RetrofitClient
import pl.matkmiec.mobile.utils.BackupManager

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
                    val sortedList = list.sortedByDescending { it.createdAt }
                    _uiState.value = DashboardState.Success(sortedList)
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
                    fetchBackups()
                } else {
                    fetchBackups()
                }
            } catch (e: Exception) {
                // handle error silently
                fetchBackups()
            }
        }
    }

    fun createBackup(type: String, context: Context) {
        _uiState.value = DashboardState.Loading
        viewModelScope.launch {
            try {
                val payloadData = if (type == "SMS") {
                    BackupManager.backupSms(context)
                } else {
                    BackupManager.backupContacts(context)
                }

                val request = BackupUploadDto(type = type, payload = payloadData)
                val response = RetrofitClient.backupApi.uploadBackup(request)
                if (response.isSuccessful) {
                    fetchBackups()
                } else {
                    _uiState.value = DashboardState.Error("Failed to upload $type backup")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error("Creation error: ${e.message}")
            }
        }
    }

    fun restoreBackup(backupId: String, type: String, context: Context) {
        _uiState.value = DashboardState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.backupApi.getBackup(backupId)
                if (response.isSuccessful && response.body() != null) {
                    val payload = response.body()!!.payload
                    if (type == "SMS") {
                        BackupManager.restoreSms(context, payload)
                    } else {
                        BackupManager.restoreContacts(context, payload)
                    }
                    _uiState.value = DashboardState.Error("Successfully restored $type!")
                    // Delaying refresh to clear success message
                    kotlinx.coroutines.delay(2000)
                    fetchBackups()
                } else {
                    _uiState.value = DashboardState.Error("Failed to load backup payload")
                }
            } catch (e: Exception) {
                 _uiState.value = DashboardState.Error("Restore error: ${e.message}")
            }
        }
    }
}


