package com.stadiolinks.kitabu.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDashboardScreen(onNavigateToCatalog: (String) -> Unit, onNavigateToLogin: (String) -> Unit) {

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

    onNavigateToCatalog: (String) -> Unit,
    onNavigateToLogin: (String) -> Unit

) {

    composable<DashboardDestination> {

        ReservationDashboardScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToLogin = onNavigateToLogin

        )

    }

}

fun NavController.navigateToDashboard() {

    navigate(DashboardDestination)

}