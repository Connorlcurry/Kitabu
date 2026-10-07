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
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel

) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {

        when (authState) {

            is AuthState.RegisterSuccess -> onNavigateToCatalog()
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

                title = { Text("Register a New Account") }

            )

        }

    ) { paddingValues ->

        Column(

            modifier = Modifier.padding(paddingValues)

        ) {

            OutlinedTextField(

                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }

            )

            Spacer(modifier = Modifier.height(8.dp))

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

                    if (username.isBlank() || email.isBlank() || password.isBlank()) {

                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()

                    } else {

                        val newUser = UserEntity(userName = username, email = email, password = password)
                        authViewModel.register(newUser)

                    }

                }

            ) {

                Text(text = "Login")

            }

            TextButton(onClick = onNavigateToLogin) {

                Text(text = "Already have an account? Login here", color = Color(0xffd2a622))

            }

        }

    }

}

@Serializable
object RegisterDestination

fun NavGraphBuilder.registerScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToLogin: () -> Unit

) {

    composable<RegisterDestination> {

        val context = LocalContext.current.applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = UserRepository(database.userDao(), context)
        val factory = AuthViewModelFactory(repository)
        val authViewModel: AuthViewModel = viewModel(factory = factory)

        RegisterScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToLogin = onNavigateToLogin,
            authViewModel = authViewModel

        )

    }

}

fun NavController.navigateToRegister() {

    navigate(RegisterDestination)

}