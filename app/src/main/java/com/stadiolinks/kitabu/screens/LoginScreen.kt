package com.stadiolinks.kitabu.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stadiolinks.kitabu.AuthState
import com.stadiolinks.kitabu.AuthViewModel
import com.stadiolinks.kitabu.AuthViewModelFactory
import com.stadiolinks.kitabu.data.database.AppDatabase
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel,

) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {

        when (authState) {

            is AuthState.LoginSuccess -> onNavigateToCatalog()
            is AuthState.Error -> Toast.makeText(
                context,
                (authState as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()

            else -> Unit

        }

    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text("Login") }

            )

        }

    ) { paddingValues ->

        Column(

            modifier = Modifier.padding(paddingValues)

        ) {

            OutlinedTextField(

                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }

            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(

                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()

            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(

                onClick = {

                    if (email.isBlank() || password.isBlank()) {

                        Toast.makeText(context, "Please enter both email and password.", Toast.LENGTH_SHORT).show()

                    } else {

                        authViewModel.login(email, password)

                    }

                }

            ) {

                Text(text = "Login")

            }

            TextButton(onClick = onNavigateToRegister) {

                Text(text = "Don't have an account? Register here", color = Color(0xffd2a622))

            }

        }

    }

}

@Serializable
data object LoginDestination

fun NavGraphBuilder.loginScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToRegister: () -> Unit

) {

    composable<LoginDestination> {

        val context = LocalContext.current.applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = UserRepository(database.userDao(), context)
        val factory = AuthViewModelFactory(repository)
        val authViewModel: AuthViewModel = viewModel(factory = factory)

        LoginScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToRegister = onNavigateToRegister,
            authViewModel = authViewModel

        )

    }

}

fun NavController.navigateToLogin() {

    navigate(LoginDestination)

}