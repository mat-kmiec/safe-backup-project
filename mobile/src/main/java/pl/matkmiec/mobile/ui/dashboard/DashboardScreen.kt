package pl.matkmiec.mobile.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.matkmiec.mobile.api.BackupListDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "SMS", "CONTACTS")
    
    // Context needed for interacting with content resolvers
    val context = LocalContext.current
    
    // Support dialog for backup creation choice
    var showCreateDialog by remember { mutableStateOf(false) }
    var restoreBackupId by remember { mutableStateOf<Pair<String, String>?>(null) } // id to type

    val permissions = arrayOf(
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.READ_SMS"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Backups", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.fetchBackups() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh backups")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Backup")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (uiState) {
                is DashboardState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = (uiState as DashboardState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                is DashboardState.Success -> {
                    val allBackups = (uiState as DashboardState.Success).backups
                    val filteredBackups = when (selectedTabIndex) {
                        1 -> allBackups.filter { it.type.equals("SMS", ignoreCase = true) }
                        2 -> allBackups.filter { it.type.equals("CONTACTS", ignoreCase = true) }
                        else -> allBackups
                    }

                    if (filteredBackups.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No backups found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(filteredBackups) { backup ->
                                BackupCard(
                                    backup = backup,
                                    onDelete = { viewModel.deleteBackup(backup.id) },
                                    onRestore = { restoreBackupId = Pair(backup.id, backup.type) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                title = { Text("Create Backup") },
                text = { Text("What kind of backup would you like to create?") },
                confirmButton = {
                    TextButton(onClick = { 
                        viewModel.createBackup("SMS", context)
                        showCreateDialog = false 
                    }) {
                        Text("SMS")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        viewModel.createBackup("CONTACTS", context)
                        showCreateDialog = false 
                    }) {
                        Text("CONTACTS")
                    }
                }
            )
        }

        if (restoreBackupId != null) {
            AlertDialog(
                onDismissRequest = { restoreBackupId = null },
                icon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                title = { Text("Restore Backup") },
                text = { Text("Are you sure you want to download and restore this ${restoreBackupId?.second} backup to your device?") },
                confirmButton = {
                    Button(onClick = { 
                        viewModel.restoreBackup(restoreBackupId!!.first, restoreBackupId!!.second, context)
                        restoreBackupId = null 
                    }) {
                        Text("Restore")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { restoreBackupId = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun BackupCard(backup: BackupListDto, onDelete: () -> Unit, onRestore: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (backup.type.equals("SMS", true)) Icons.AutoMirrored.Filled.Message else Icons.Default.Contacts
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${backup.type} Backup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = backup.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DataUsage,
                        contentDescription = "Size",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${backup.payloadSize} bytes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!backup.type.equals("SMS", true)) {
                IconButton(onClick = onRestore) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Restore Backup",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Backup",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
