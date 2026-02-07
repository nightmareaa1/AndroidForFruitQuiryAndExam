package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import com.example.userauth.viewmodel.ModelViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementScreen(
    onBack: () -> Unit,
    viewModel: ModelViewModel = hiltViewModel()
) {
    val models by viewModel.models.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf<EvaluationModel?>(null) }

    // Force refresh when entering the screen
    LaunchedEffect(Unit) {
        viewModel.loadModels()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("评价模型管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "添加模型")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(models) { model ->
                    ModelCard(
                        model = model,
                        onEdit = {
                            selectedModel = model
                            showEditDialog = true
                        },
                        onDelete = {
                            selectedModel = model
                            showDeleteConfirm = true
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ModelEditDialog(
            model = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, _, parameters ->
                viewModel.addModel(name, parameters)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && selectedModel != null) {
        ModelEditDialog(
            model = selectedModel,
            onDismiss = {
                showEditDialog = false
                selectedModel = null
            },
            onConfirm = { name, description, parameters ->
                selectedModel?.let { model ->
                    viewModel.updateModel(
                        model.copy(
                            name = name,
                            description = description,
                            parameters = parameters
                        )
                    )
                }
                showEditDialog = false
                selectedModel = null
            }
        )
    }

    if (showDeleteConfirm && selectedModel != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
                selectedModel = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除评价模型 ${selectedModel?.name} 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedModel?.let { viewModel.deleteModel(it.id) }
                        showDeleteConfirm = false
                        selectedModel = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    selectedModel = null
                }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelCard(
    model: EvaluationModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (model.description.isNotBlank()) {
                        Text(
                            text = model.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (model.parameters.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = "评价维度:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                model.parameters.forEach { param ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = param.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "权重${param.weight}%  满分${param.maxScore}分",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelEditDialog(
    model: EvaluationModel?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, parameters: List<EvaluationParameter>) -> Unit
) {
    var name by remember { mutableStateOf(model?.name ?: "") }
    var description by remember { mutableStateOf(model?.description ?: "") }
    var parameters by remember { mutableStateOf(model?.parameters?.toMutableList() ?: mutableListOf()) }
    var showAddParameter by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val totalWeight: Int = parameters.sumOf { it.weight }
    val isValid: Boolean = name.isNotBlank() && totalWeight == 100

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (model == null) "添加评价模型" else "编辑评价模型") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("模型名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("模型描述") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "评价维度 (${parameters.size})",
                        style = MaterialTheme.typography.titleSmall
                    )
                    TextButton(onClick = { showAddParameter = true }) {
                        Text("添加维度")
                    }
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parameters.size) { index ->
                        val param = parameters[index]
                        ParameterItem(
                            parameter = param,
                            onDelete = {
                                parameters = parameters.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }
                
                // Total weight display
                Text(
                    text = "总权重: $totalWeight/100",
                    color = if (totalWeight == 100) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        errorMessage = null
                        onConfirm(name, description, parameters)
                    } else {
                        errorMessage = if (totalWeight != 100) "权重必须为100，当前为$totalWeight" else "请输入模型名称"
                    }
                },
                enabled = name.isNotBlank() && parameters.isNotEmpty()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )

    if (showAddParameter) {
        AddParameterDialog(
            onDismiss = { showAddParameter = false },
            onConfirm = { paramName, weight, maxScore ->
                parameters = parameters.toMutableList().apply {
                    add(
                        EvaluationParameter(
                            id = UUID.randomUUID().toString(),
                            name = paramName,
                            weight = weight,
                            maxScore = maxScore
                        )
                    )
                }
                showAddParameter = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParameterItem(
    parameter: EvaluationParameter,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = parameter.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "权重${parameter.weight}%  满分${parameter.maxScore}分",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddParameterDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, weight: Int, maxScore: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var maxScore by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加评价维度") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("维度名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() } },
                    label = { Text("权重(%)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = maxScore,
                    onValueChange = { maxScore = it.filter { c -> c.isDigit() } },
                    label = { Text("满分") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name,
                        weight.toIntOrNull() ?: 0,
                        maxScore.toIntOrNull() ?: 10
                    )
                },
                enabled = name.isNotBlank() && weight.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
