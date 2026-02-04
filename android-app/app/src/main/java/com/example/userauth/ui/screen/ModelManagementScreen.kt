package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userauth.viewmodel.ModelViewModel
import com.example.userauth.data.model.EvaluationModel

@androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementScreen(onBack: () -> Unit, viewModel: ModelViewModel = viewModel()) {
    val models by viewModel.models.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("评价模型管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
        ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(models) { m: EvaluationModel ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${m.name} (weight=${m.weight})")
                        }
                    }
                }
            }
        }
    }
}
