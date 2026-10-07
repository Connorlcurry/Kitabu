package com.stadiolinks.kitabu.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stadiolinks.kitabu.AuthViewModel
import com.stadiolinks.kitabu.AuthViewModelFactory
import com.stadiolinks.kitabu.data.database.AppDatabase
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDashboardScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel

) {

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text("Book Reservation Dashboard") }

            )

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
    onNavigateToLogin: () -> Unit

) {

    composable<DashboardDestination> {

        val context = LocalContext.current.applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = UserRepository(database.userDao(), context)
        val factory = AuthViewModelFactory(repository)
        val authViewModel: AuthViewModel = viewModel(factory = factory)

        ReservationDashboardScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToLogin = onNavigateToLogin,
            authViewModel = authViewModel

        )

    }

}

fun NavController.navigateToDashboard() {

    navigate(DashboardDestination)

}