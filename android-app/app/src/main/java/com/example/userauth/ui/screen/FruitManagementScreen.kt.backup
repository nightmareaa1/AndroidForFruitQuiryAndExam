package com.example.userauth.ui.screen

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.FruitDataManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FruitManagementScreen(
    onBack: () -> Unit,
    viewModel: FruitDataManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(0) }

    var showAddDataTypeDialog by remember { mutableStateOf(false) }
    var selectedDataTypeToDelete by remember { mutableStateOf<String?>(null) }
    var showAddFruitDialog by remember { mutableStateOf(false) }
    var selectedFruitToDelete by remember { mutableStateOf<com.example.userauth.data.api.FruitOption?>(null) }
    var showDeleteTableDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.success) {
        uiState.success?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
        }
    }

    val context = LocalContext.current

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val contentResolver = context.contentResolver
            var fileName: String? = null
            if (selectedUri.scheme == "content") {
                try {
                    contentResolver.query(selectedUri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (index >= 0) {
                                fileName = cursor.getString(index)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
            if (fileName == null) {
                fileName = selectedUri.path
                val cut = fileName?.lastIndexOf('/')
                if (cut != null && cut != -1) {
                    fileName = fileName?.substring(cut + 1)
                }
            }
            val mimeType = try {
                contentResolver.getType(selectedUri)
            } catch (e: Exception) {
                null
            }
            val isCsv = fileName?.endsWith(".csv", ignoreCase = true) == true ||
                        mimeType == "text/csv" ||
                        mimeType == "text/comma-separated-values" ||
                        mimeType == "application/csv" ||
                        mimeType == "application/vnd.ms-excel"
            if (isCsv) {
                val dataType = uiState.selectedDataType
                if (dataType != null) {
                    viewModel.uploadFile(selectedUri, dataType)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("水果数据管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("字段管理") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("表格管理") }
                )
            }

            when (selectedTab) {
                0 -> FieldManagementContent(
                    uiState = uiState,
                    onAddDataType = { showAddDataTypeDialog = true },
                    onDeleteDataType = { selectedDataTypeToDelete = it },
                    onAddFruit = { showAddFruitDialog = true },
                    onDeleteFruit = { selectedFruitToDelete = it }
                )
                1 -> TableManagementContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    onUploadFile = { filePicker.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv", "*/*")) },
                    onDeleteTable = { showDeleteTableDialog = true }
                )
            }
        }
    }

    if (showAddDataTypeDialog) {
        AddDataTypeDialog(
            onDismiss = { showAddDataTypeDialog = false },
            onConfirm = { dataType ->
                viewModel.createDataType(dataType, "default", null)
                showAddDataTypeDialog = false
            }
        )
    }

    if (showAddFruitDialog) {
        AddFruitDialog(
            onDismiss = { showAddFruitDialog = false },
            onConfirm = { fruitName ->
                viewModel.createFruit(fruitName)
                showAddFruitDialog = false
            }
        )
    }

    if (selectedDataTypeToDelete != null) {
        AlertDialog(
            onDismissRequest = { selectedDataTypeToDelete = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除数据类型 \"$selectedDataTypeToDelete\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDataTypeToDelete?.let { viewModel.deleteDataType(it) }
                        selectedDataTypeToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDataTypeToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }

    if (selectedFruitToDelete != null) {
        AlertDialog(
            onDismissRequest = { selectedFruitToDelete = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除水果 \"${selectedFruitToDelete?.name}\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedFruitToDelete?.id?.let { viewModel.deleteFruit(it) }
                        selectedFruitToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFruitToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }

    if (showDeleteTableDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTableDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除整个表格吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTable()
                        showDeleteTableDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTableDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun DataTypeCard(
    dataType: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dataType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "点击在查询页面管理此类型数据",
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

@Composable
private fun AddDataTypeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var dataType by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加数据类型") },
        text = {
            OutlinedTextField(
                value = dataType,
                onValueChange = { dataType = it },
                label = { Text("数据类型名称") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(dataType)
                },
                enabled = dataType.isNotBlank()
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
}

@Composable
private fun AddFruitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var fruitName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加水果") },
        text = {
            OutlinedTextField(
                value = fruitName,
                onValueChange = { fruitName = it },
                label = { Text("水果名称") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(fruitName)
                },
                enabled = fruitName.isNotBlank()
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
}

@Composable
private fun FieldManagementContent(
    uiState: com.example.userauth.viewmodel.FruitDataManagementUiState,
    onAddDataType: () -> Unit,
    onDeleteDataType: (String) -> Unit,
    onAddFruit: () -> Unit,
    onDeleteFruit: (com.example.userauth.data.api.FruitOption) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "数据类型",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onAddDataType) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("添加")
                }
            }
        }

        items(uiState.dataTypes) { dataType ->
            DataTypeCard(
                dataType = dataType,
                onDelete = { onDeleteDataType(dataType) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "水果种类",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onAddFruit) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("添加")
                }
            }
        }

        items(uiState.fruits) { fruit ->
            FruitCard(
                fruit = fruit,
                onDelete = { onDeleteFruit(fruit) }
            )
        }
    }
}

@Composable
private fun TableManagementContent(
    uiState: com.example.userauth.viewmodel.FruitDataManagementUiState,
    viewModel: FruitDataManagementViewModel,
    onUploadFile: () -> Unit,
    onDeleteTable: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "查询条件",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    var expandedDataType by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expandedDataType = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.selectedDataType ?: "选择数据类型",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        DropdownMenu(
                            expanded = expandedDataType,
                            onDismissRequest = { expandedDataType = false }
                        ) {
                            uiState.dataTypes.forEach { dataType ->
                                DropdownMenuItem(
                                    text = { Text(dataType) },
                                    onClick = {
                                        viewModel.selectDataType(dataType)
                                        expandedDataType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var expandedFruit by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expandedFruit = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.selectedFruit?.name ?: "选择水果",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        DropdownMenu(
                            expanded = expandedFruit,
                            onDismissRequest = { expandedFruit = false }
                        ) {
                            uiState.fruits.forEach { fruit ->
                                DropdownMenuItem(
                                    text = { Text(fruit.name) },
                                    onClick = {
                                        viewModel.selectFruit(fruit)
                                        expandedFruit = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.checkTableExists() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.selectedDataType != null && uiState.selectedFruit != null
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("查询表格")
                    }
                }
            }
        }

        if (uiState.selectedDataType != null && uiState.selectedFruit != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.tableExists) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "表格已存在",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${uiState.selectedDataType} - ${uiState.selectedFruit?.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onDeleteTable,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("删除表格")
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "表格不存在",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${uiState.selectedDataType} - ${uiState.selectedFruit?.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onUploadFile,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("上传CSV创建表格")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FruitCard(
    fruit: com.example.userauth.data.api.FruitOption,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fruit.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

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
