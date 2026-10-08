package com.stadiolinks.kitabu.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.stadiolinks.kitabu.data.repository.LibraryRepository
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val configuration = LocalConfiguration.current

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

        containerColor = Color(0xff0B3954),
        topBar = {

            TopAppBar(

                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = Color(0xff0B3954)

                )

            )

        }

    ) { paddingValues ->

        Column(

            modifier = Modifier.fillMaxWidth(0.75f)
                .padding(paddingValues),
            horizontalAlignment = Alignment.Start

        ) {

            Text(

                text = "Create an Account and Start Using Kitabu Today.",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 40.sp,
                modifier = Modifier.padding(start = 16.dp, top = 0.dp)

            )

        }

        ModalBottomSheet(

            onDismissRequest = {},
            sheetGesturesEnabled = false,
            properties = ModalBottomSheetProperties(
                shouldDismissOnClickOutside = false
            ),
            dragHandle = null,
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth(),
            scrimColor = Color.Black.copy(alpha = 0.0f)

        ) {

            // Box containing the login form
            Box(

                modifier = Modifier
                    .fillMaxWidth().height((configuration.screenHeightDp * 0.45f).dp)
                    .padding(16.dp)

            ) {

                Column(

                    modifier= Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {

                    Text(

                        text = "Register",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold

                    )

                    TextButton(onClick = onNavigateToLogin) {

                        Text(

                            text = buildAnnotatedString {
                                append("Already have an account? ")
                                withStyle(style = SpanStyle(color = Color(0xffFF6663))) {
                                    append("Login here.")
                                }
                            }

                        )

                    }

                    OutlinedTextField(

                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xff0B3954),
                            focusedLabelColor = Color(0xff0B3954)

                        ),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(50.dp)

                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(

                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xff0B3954),
                            focusedLabelColor = Color(0xff0B3954)

                        ),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(50.dp)

                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(

                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xff0B3954),
                            focusedLabelColor = Color(0xff0B3954)

                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(50.dp)

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

                        },
                        colors = ButtonDefaults.buttonColors(

                            containerColor = Color(0xff0B3954)

                        ),
                        modifier = Modifier.fillMaxWidth(0.85f)

                    ) {

                        Text(text = "Register")

                    }

                }

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
        val userRepository = UserRepository(database.userDao(), context)
        val libraryRepository = LibraryRepository(database.libraryDao(), context)
        val factory = AuthViewModelFactory(userRepository, libraryRepository)
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