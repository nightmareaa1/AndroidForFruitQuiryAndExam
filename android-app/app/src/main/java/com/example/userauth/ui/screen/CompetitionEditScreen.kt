package com.example.userauth.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    val isLoading by viewModel.isLoading.collectAsState()

    // Force refresh when entering the screen
    LaunchedEffect(competitionId) {
        viewModel.loadCompetitions()
        modelViewModel.loadModels()
    }

    // Find competition from loaded list
    val competition = remember(competitions, competitionId) {
        competitions.find { it.id == competitionId }
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

    var name by remember { mutableStateOf(competition.name) }
    var description by remember { mutableStateOf(competition.description) }
    var deadline by remember { mutableStateOf(competition.deadline) }
    var selectedModelId by remember { mutableStateOf(competition.modelId) }
    var showModelDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("赛事名称") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("赛事介绍") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

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

            Spacer(modifier = Modifier.weight(1f))

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
                enabled = name.isNotBlank() && deadline.isNotBlank()
            ) {
                Text("保存修改")
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
