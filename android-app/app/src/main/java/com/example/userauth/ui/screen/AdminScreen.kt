package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.UserViewModel

/**
 * Admin panel screen UI (skeleton for task 22.2)
 * Shows admin options and a back navigation button.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onNavigateToModelManagement: (() -> Unit)? = null,
    onNavigateToCompetitionManagement: (() -> Unit)? = null,
    viewModel: UserViewModel = hiltViewModel()
) {
    val isAdmin = viewModel.isCurrentUserAdmin()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("管理员面板") },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isAdmin) {
                Text("管理员工具", fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp))
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onNavigateToModelManagement?.invoke() },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("模型管理入口")
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onNavigateToCompetitionManagement?.invoke() },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("赛事管理入口")
                }
            } else {
                Text("仅管理员可访问", fontSize = 16.sp)
            }
        }
    }
}
