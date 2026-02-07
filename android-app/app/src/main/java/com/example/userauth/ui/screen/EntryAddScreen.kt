package com.example.userauth.ui.screen

import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.EntryAddViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryAddScreen(
    competitionId: Long,
    competitionName: String,
    onBack: () -> Unit,
    onSubmissionSuccess: () -> Unit,
    viewModel: EntryAddViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateImageUri(it) }
    }
    
    // Set competition ID when screen opens
    LaunchedEffect(competitionId) {
        viewModel.setCompetitionId(competitionId)
    }
    
    // Navigate back on successful submission
    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            onSubmissionSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("提交参赛作品") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Competition info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "赛事: $competitionName",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            // Entry name input
            OutlinedTextField(
                value = uiState.entryName,
                onValueChange = { viewModel.updateEntryName(it) },
                label = { Text("参赛作品名称 *") },
                placeholder = { Text("请输入作品名称") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error?.contains("名称") == true
            )
            
            // Description input
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("作品简介") },
                placeholder = { Text("请输入作品简介（可选）") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Image picker
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.imageUri != null) {
                        // Load image using basic Android methods
                        var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
                        
                        LaunchedEffect(uiState.imageUri) {
                            try {
                                val inputStream = uiState.imageUri?.let {
                                    android.content.ContextWrapper(android.app.Application())
                                        .baseContext.contentResolver.openInputStream(it)
                                }
                                inputStream?.use { stream ->
                                    bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                                }
                            } catch (e: Exception) {
                                bitmap = null
                            }
                        }
                        
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "点击添加作品图片（可选）",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Submit button
            Button(
                onClick = { viewModel.submitEntry() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.entryName.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("提交")
                }
            }
        }
    }
}
