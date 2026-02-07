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
import com.example.userauth.viewmodel.CompetitionManagementViewModel
import com.example.userauth.viewmodel.ModelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionAddScreen(
    onBack: () -> Unit,
    viewModel: CompetitionManagementViewModel = hiltViewModel(),
    modelViewModel: ModelViewModel = hiltViewModel()
) {
    val models by modelViewModel.models.collectAsState()

    // Force refresh when entering the screen
    LaunchedEffect(Unit) {
        modelViewModel.loadModels()
    }

    var name by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedModelId by remember { mutableStateOf<String?>(null) }
    var showModelDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    selectedModelId?.let { modelId ->
                        viewModel.addCompetition(
                            name = name,
                            deadline = deadline,
                            modelId = modelId.toLong()
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && deadline.isNotBlank() && selectedModelId != null
            ) {
                Text("添加赛事")
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
