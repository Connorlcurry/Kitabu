package com.stadiolinks.kitabu.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigateToCatalog: (String) -> Unit, onNavigateToRegister: (String) -> Unit) {

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


            Button(onClick = { onNavigateToCatalog("") }) {

                Text(text = "Login")

            }

        }

    }

}

@Serializable
data object LoginDestination

fun NavGraphBuilder.loginScreen(

    onNavigateToCatalog: (String) -> Unit,
    onNavigateToRegister: (String) -> Unit

) {

    composable<LoginDestination> {

        LoginScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToRegister = onNavigateToRegister

        )

    }

}

fun NavController.navigateToLogin() {

    navigate(LoginDestination)

}