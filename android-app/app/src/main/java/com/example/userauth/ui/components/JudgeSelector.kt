package com.example.userauth.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.userauth.data.api.dto.UserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JudgeSelector(
    users: List<UserDto>,
    filteredUsers: List<UserDto>,
    selectedJudgeIds: List<Long>,
    existingJudgeIds: List<Long> = emptyList(),
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleJudge: (Long) -> Unit,
    onRemoveExistingJudge: (Long) -> Unit = {},
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("搜索评委") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "搜索")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "清除")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 选择按钮
        Box(modifier = Modifier.fillMaxWidth()) {
            val selectedText = if (selectedJudgeIds.isEmpty()) {
                if (isEditMode) "添加新评委" else "选择评委（可选）"
            } else {
                "已选择 ${selectedJudgeIds.size} 位评委"
            }
            OutlinedTextField(
                value = selectedText,
                onValueChange = { },
                label = { Text("评委") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDropdown = true },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "选择")
                    }
                }
            )

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (filteredUsers.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("没有找到用户", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        onClick = { }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 350.dp)
                    ) {
                        items(filteredUsers) { user ->
                            val isSelected = selectedJudgeIds.contains(user.id)
                            val isExisting = existingJudgeIds.contains(user.id)
                            
                            DropdownMenuItem(
                                enabled = !isExisting,
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { onToggleJudge(user.id) },
                                            enabled = !isExisting
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = user.username ?: "未知用户",
                                                color = if (isExisting) 
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) 
                                                else 
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (user.roles.contains("ADMIN")) "管理员" else "用户",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isExisting) 
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) 
                                                else 
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (isExisting) {
                                                Text(
                                                    text = "已是评委",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = { if (!isExisting) onToggleJudge(user.id) }
                            )
                        }
                    }
                }
            }
        }

        // 显示已选评委（添加模式）
        if (!isEditMode && selectedJudgeIds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
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
                            Text(
                                text = user.username ?: "未知用户",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            TextButton(onClick = { onToggleJudge(user.id) }) {
                                Text("移除")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingJudgesList(
    users: List<UserDto>,
    existingJudgeIds: List<Long>,
    onRemoveJudge: (Long) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val existingJudges = users.filter { existingJudgeIds.contains(it.id) }

    if (existingJudgeIds.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "已有评委",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (existingJudges.isEmpty()) {
                    Text(
                        text = "加载中...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                    )
                } else {
                    existingJudges.forEach { user ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user.username ?: "未知用户",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                TextButton(onClick = { onRemoveJudge(user.id) }) {
                                    Text("移除")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
