package com.example.userauth.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userauth.viewmodel.FruitNutritionViewModel
import com.example.userauth.data.model.QueryDataItem
import com.example.userauth.data.model.FruitNutrition
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FruitNutritionScreen(
    onBack: () -> Unit,
    onFruitClick: (String) -> Unit = {},
    viewModel: FruitNutritionViewModel = viewModel()
) {
    val fruits by viewModel.fruits.collectAsState()
    val results by viewModel.queryResults.collectAsState()

    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("营养成分") }
    val typeOptions = listOf("营养成分", "风味")

    var fruitExpanded by remember { mutableStateOf(false) }
    var selectedFruit by remember { mutableStateOf("芒果") }
    val fruitOptions = listOf("芒果", "香蕉")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("水果营养查询") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type dropdown
                Box {
                    Text(text = selectedType, modifier = Modifier
                        .padding(12.dp)
                        .testTag("type-dropdown")
                        .clickable { typeExpanded = true }
                    )
                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        typeOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(text = opt) },
                                onClick = {
                                    selectedType = opt
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
                // Fruit dropdown
                Box {
                Text(text = selectedFruit, modifier = Modifier
                    .padding(12.dp)
                    .testTag("fruit-dropdown")
                    .clickable { fruitExpanded = true })
                    DropdownMenu(
                        expanded = fruitExpanded,
                        onDismissRequest = { fruitExpanded = false }
                    ) {
                        fruitOptions.forEach { f ->
                            DropdownMenuItem(
                                text = { Text(text = f) },
                                onClick = {
                                    selectedFruit = f
                                    fruitExpanded = false
                                }
                            )
                        }
                    }
                }
                Button(onClick = { viewModel.query(selectedType, selectedFruit) }, modifier = Modifier.testTag("query-button")) {
                    Text("查询")
                }
            }

            // Results table header
            if (results.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("results-header")) {
                    Text(text = "成分名称", modifier = Modifier.weight(1f))
                    Text(text = "数值", modifier = Modifier.width(120.dp))
                }
                Divider()
                LazyColumn {
                    items(results) { item: QueryDataItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = item.componentName, modifier = Modifier.weight(1f))
                            Text(text = item.value.toString(), modifier = Modifier.width(120.dp))
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("无查询结果", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
