@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userauth.viewmodel.CompetitionManagementViewModel
import com.example.userauth.data.model.Competition
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionManagementScreen(
    onBack: () -> Unit,
    onExport: (() -> Unit)? = null,
    viewModel: CompetitionManagementViewModel = viewModel()
) {
    val competitions by viewModel.competitions.collectAsState()
        Scaffold(
        // Replaced experimental TopAppBar with a simple header to avoid experimental API warnings
        topBar = {
            // empty, header drawn in content to avoid experimental API dependency
            Row(modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text("赛事管理（管理员）", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // List of competitions
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
                items(competitions) { c: Competition ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("CompetitionItem-" + c.id)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${c.name}  截止: ${c.deadline}")
                        }
                    }
                }
            }
            // Simple add form
            var name by remember { mutableStateOf("") }
            var date by remember { mutableStateOf("") }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("赛事名称") },
                    modifier = Modifier.weight(1f).testTag("CompetitionNameInput")
                )
                Spacer(Modifier.width(8.dp))
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("日期(YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f).testTag("CompetitionDateInput")
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                    if (name.isNotBlank() && date.isNotBlank()) {
                        viewModel.addCompetition(name, date)
                        name = ""; date = ""
                    }
                }, modifier = Modifier.testTag("CompetitionAddBtn")) {
                    Text("添加")
                }
            }
            // Export button for 24.4 UI test
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { onExport?.invoke() ?: Unit }, modifier = Modifier.testTag("CompetitionExportBtn")) {
                    Text("导出")
                }
            }
        }
    }
}
