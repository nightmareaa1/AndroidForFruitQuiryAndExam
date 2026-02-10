package com.example.userauth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.userauth.ui.components.GradientButton
import com.example.userauth.ui.components.DecorativeCircleBackground
import com.example.userauth.ui.theme.*
import com.example.userauth.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(loginState.isSuccess) {
        if (loginState.isSuccess) {
            onLoginSuccess()
            viewModel.clearLoginState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandGradients.LoginBackground)
    ) {
        DecorativeCircleBackground(
            size = DecorationSize.backgroundCircleLarge,
            offsetX = (-100).dp,
            offsetY = (-100).dp,
            alpha = 0.1f,
            modifier = Modifier.align(Alignment.TopStart)
        )

        DecorativeCircleBackground(
            size = DecorationSize.backgroundCircle,
            offsetX = 50.dp,
            offsetY = 100.dp,
            alpha = 0.08f,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Text(
            text = "FRUIT",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White.copy(alpha = 0.06f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding.screenLarge)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Primary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "果评赛事",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                text = "水果评价赛事管理系统",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.xlarge),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DecorationSize.stripHeight)
                            .background(BrandGradients.PrimaryGradient)
                    )

                    Column(
                        modifier = Modifier.padding(Padding.screenLarge)
                    ) {
                        Text(
                            text = "欢迎回来",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurface
                        )

                        Text(
                            text = "请登录您的账号",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("用户名") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loginState.isLoading,
                            isError = loginState.error?.contains("username", ignoreCase = true) == true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("密码") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                                        tint = OnSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loginState.isLoading,
                            isError = loginState.error?.contains("password", ignoreCase = true) == true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { }) {
                                Text(
                                    text = "忘记密码？",
                                    color = Primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        GradientButton(
                            text = "登录",
                            onClick = { viewModel.login(username.trim(), password) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = username.isNotBlank() && password.isNotBlank() && !loginState.isLoading
                        )

                        if (loginState.error != null) {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = loginState.error ?: "",
                                color = Error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "还没有账号？",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "立即注册",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
