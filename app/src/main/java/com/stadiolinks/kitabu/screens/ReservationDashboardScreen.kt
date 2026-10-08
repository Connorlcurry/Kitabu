package com.stadiolinks.kitabu.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stadiolinks.kitabu.AuthViewModel
import com.stadiolinks.kitabu.AuthViewModelFactory
import com.stadiolinks.kitabu.data.database.AppDatabase
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDashboardScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel

) {

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text("Book Reservation Dashboard") }

            )

        },
        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCatalog,
                    icon = { Icon(Icons.Default.Book, contentDescription = "Catalog") },
                    label = { Text("Catalog") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateToDashboard,
                    icon = { Icon(Icons.Default.AddChart, contentDescription = "Reservation Dashboard", tint = Color(0xff0B3954)) },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToLogin,
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                    label = { Text("Logout") }
                )

            }

        }

    ) { paddingValues ->

        Column(

            modifier = Modifier.padding(paddingValues)

        ) {}

    }

}

@Serializable
data object DashboardDestination

fun NavGraphBuilder.reservationDashboardScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit

) {

    composable<DashboardDestination> {

        val context = LocalContext.current.applicationContext
        val database = AppDatabase.getDatabase(context)
        val userRepository = UserRepository(database.userDao(), context)
        val libraryRepository = LibraryRepository(database.libraryDao(), context)
        val factory = AuthViewModelFactory(userRepository, libraryRepository)
        val authViewModel: AuthViewModel = viewModel(factory = factory)

        ReservationDashboardScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToLogin = onNavigateToLogin,
            authViewModel = authViewModel

        )

    }

}

fun NavController.navigateToDashboard() {

    navigate(DashboardDestination)

}