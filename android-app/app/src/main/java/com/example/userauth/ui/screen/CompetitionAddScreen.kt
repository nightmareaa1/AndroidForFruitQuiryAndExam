package com.example.userauth.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.CompetitionManagementViewModel
import com.example.userauth.viewmodel.ModelViewModel
import com.example.userauth.data.api.dto.UserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionAddScreen(
    onBack: () -> Unit,
    viewModel: CompetitionManagementViewModel = hiltViewModel(),
    modelViewModel: ModelViewModel = hiltViewModel()
) {
    val models by modelViewModel.models.collectAsState()
    val users by viewModel.users.collectAsState()
    val selectedJudgeIds by viewModel.selectedJudgeIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedModelId by remember { mutableStateOf<String?>(null) }
    var showModelDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showUserDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        modelViewModel.loadModels()
        viewModel.loadUsers()
        viewModel.clearSelectedJudges()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加新赛事") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("赛事名称") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { },
                        label = { Text("截止日期") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("选择")
                            }
                        }
                    )
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = models.find { it.id == selectedModelId }?.name ?: "选择评价模型 *",
                        onValueChange = { },
                        label = { Text("评价模型") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showModelDropdown = true },
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { showModelDropdown = true }) {
                                Text("选择")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = showModelDropdown,
                        onDismissRequest = { showModelDropdown = false }
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(model.name)
                                        Text(
                                            text = model.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedModelId = model.id
                                    showModelDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                if (selectedModelId != null) {
                    val selectedModel = models.find { it.id == selectedModelId }
                    selectedModel?.let { model ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "已选择: ${model.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "评价维度: ${model.parameters.joinToString { it.name }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    val selectedJudgesText = if (selectedJudgeIds.isEmpty()) {
                        "选择评委（可选）"
                    } else {
                        "已选择 ${selectedJudgeIds.size} 位评委"
                    }
                    OutlinedTextField(
                        value = selectedJudgesText,
                        onValueChange = { },
                        label = { Text("评委") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showUserDropdown = true },
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { showUserDropdown = true }) {
                                Text("选择")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = showUserDropdown,
                        onDismissRequest = { showUserDropdown = false },
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        if (users.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("加载中...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = { }
                            )
                        } else {
                            users.forEach { user ->
                                val isSelected = selectedJudgeIds.contains(user.id)
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = { viewModel.toggleJudgeSelection(user.id) }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(user.username)
                                                Text(
                                                    text = if (user.roles.contains("ADMIN")) "管理员" else "用户",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = { viewModel.toggleJudgeSelection(user.id) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                if (selectedJudgeIds.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "已选评委",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            users.filter { selectedJudgeIds.contains(it.id) }.forEach { user ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(user.username, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    TextButton(onClick = { viewModel.toggleJudgeSelection(user.id) }) {
                                        Text("移除")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Button(
                    onClick = {
                        selectedModelId?.let { modelId ->
                            viewModel.addCompetition(
                                name = name,
                                deadline = deadline,
                                modelId = modelId.toLong(),
                                judgeIds = selectedJudgeIds.ifEmpty { null }
                            )
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && deadline.isNotBlank() && selectedModelId != null && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加中...")
                    } else {
                        Text("添加赛事")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { year, month, day ->
                deadline = String.format("%04d-%02d-%02d", year, month + 1, day)
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = java.util.Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        onDateSelected(
                            calendar.get(java.util.Calendar.YEAR),
                            calendar.get(java.util.Calendar.MONTH),
                            calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        )
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
