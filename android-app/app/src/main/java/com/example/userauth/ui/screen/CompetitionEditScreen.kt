package com.example.userauth.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.data.model.Competition
import com.example.userauth.viewmodel.CompetitionManagementViewModel
import com.example.userauth.viewmodel.ModelViewModel
import com.example.userauth.ui.components.ExistingJudgesList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionEditScreen(
    competitionId: Long,
    onBack: () -> Unit,
    viewModel: CompetitionManagementViewModel = hiltViewModel(),
    modelViewModel: ModelViewModel = hiltViewModel()
) {
    val competitions by viewModel.competitions.collectAsState()
    val models by modelViewModel.models.collectAsState()
    val users by viewModel.users.collectAsState()
    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val selectedJudgeIds by viewModel.selectedJudgeIds.collectAsState()
    val existingJudgeIds by viewModel.existingJudgeIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedModelId by remember { mutableStateOf(0L) }
    var showModelDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Force refresh when entering the screen
    LaunchedEffect(competitionId) {
        viewModel.loadCompetitions()
        modelViewModel.loadModels()
        viewModel.loadUsers()
        viewModel.loadCompetitionDetail(competitionId)
    }

    // Find competition from loaded list
    val competition = remember(competitions, competitionId) {
        competitions.find { it.id == competitionId }
    }

    // Update form when competition is loaded
    LaunchedEffect(competition) {
        competition?.let {
            name = it.name
            description = it.description
            deadline = it.deadline
            selectedModelId = it.modelId
        }
    }

    // Show loading or error state while waiting for competition to load
    if (competition == null && isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return@CompetitionEditScreen
    }

    if (competition == null) {
        LaunchedEffect(Unit) {
            onBack()
        }
        return@CompetitionEditScreen
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑赛事") },
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
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("赛事介绍") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
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
                        value = models.find { it.id == selectedModelId.toString() }?.name ?: "选择评价模型",
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
                                text = { Text(model.name) },
                                onClick = {
                                    selectedModelId = model.id.toLong()
                                    showModelDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // 已有评委列表
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "已有评委",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExistingJudgesList(
                    users = users,
                    existingJudgeIds = existingJudgeIds,
                    onRemoveJudge = { judgeId ->
                        viewModel.removeExistingJudge(competitionId, judgeId)
                    },
                    isLoading = isLoading
                )
            }

            // 添加评委
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "添加评委",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    label = { Text("搜索用户") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "搜索")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "清除")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 搜索结果列表
                val usersToShow = if (searchQuery.isBlank()) {
                    emptyList()
                } else {
                    filteredUsers.filter { !existingJudgeIds.contains(it.id) }
                }
                
                if (usersToShow.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "搜索结果",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            usersToShow.forEach { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = user.username ?: "未知用户",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (user.roles.contains("ADMIN")) "管理员" else "用户",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            // 直接添加这个用户为评委
                                            viewModel.addNewJudges(competitionId, listOf(user.id))
                                            // 清空搜索框
                                            viewModel.setSearchQuery("")
                                        },
                                        enabled = !isLoading
                                    ) {
                                        Text("添加")
                                    }
                                }
                            }
                        }
                    }
                } else if (searchQuery.isNotBlank()) {
                    Text(
                        text = "未找到用户",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateCompetition(
                            competition.copy(
                                name = name,
                                description = description,
                                deadline = deadline,
                                modelId = selectedModelId
                            )
                        )
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && deadline.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("保存中...")
                    } else {
                        Text("保存修改")
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
