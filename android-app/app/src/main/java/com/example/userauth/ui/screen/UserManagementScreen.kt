package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.data.api.dto.UserDto
import com.example.userauth.viewmodel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var toDeleteUser by remember { mutableStateOf<UserDto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用户管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "创建用户")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading && users.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) { user ->
                    UserRow(
                        user = user,
                        currentUsername = viewModel.currentUsername,
                        onToggleRole = { viewModel.toggleAdminRole(user) },
                        onDelete = { toDeleteUser = user }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { username, password, isAdmin ->
                viewModel.createUser(username, password, isAdmin) {
                    showCreateDialog = false
                }
            }
        )
    }

    if (toDeleteUser != null) {
        AlertDialog(
            onDismissRequest = { toDeleteUser = null },
            title = { Text("确认删除") },
            text = { Text("确定删除用户 ${toDeleteUser?.username ?: ""} 吗？") },
            confirmButton = {
                TextButton(onClick = {
                    toDeleteUser?.let { viewModel.deleteUser(it.id) }
                    toDeleteUser = null
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { toDeleteUser = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun UserRow(
    user: UserDto,
    currentUsername: String?,
    onToggleRole: () -> Unit,
    onDelete: () -> Unit
) {
    val isAdmin = user.roles.contains("ADMIN")
    val isCurrentUser = currentUsername != null && currentUsername == user.username

    androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = user.username ?: "未知用户", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAdmin) "角色: 管理员" else "角色: 普通用户",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onToggleRole, enabled = !isCurrentUser) {
                    Text(if (isAdmin) "设为普通用户" else "设为管理员")
                }
                TextButton(onClick = onDelete, enabled = !isCurrentUser) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Text("删除", modifier = Modifier.padding(start = 4.dp))
                }
            }
            if (isCurrentUser) {
                Text(
                    text = "当前登录用户不可删除或降权",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CreateUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (username: String, password: String, isAdmin: Boolean) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("创建用户") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                    Text("设为管理员")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(username.trim(), password, isAdmin) },
                enabled = username.isNotBlank() && password.length >= 8
            ) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
