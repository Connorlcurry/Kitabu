package com.stadiolinks.kitabu

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.stadiolinks.kitabu.screens.LoginDestination
import com.stadiolinks.kitabu.screens.catalogScreen
import com.stadiolinks.kitabu.screens.loginScreen
import com.stadiolinks.kitabu.screens.navigateToCatalog
import com.stadiolinks.kitabu.screens.navigateToDashboard
import com.stadiolinks.kitabu.screens.navigateToLogin
import com.stadiolinks.kitabu.screens.navigateToRegister
import com.stadiolinks.kitabu.screens.registerScreen
import com.stadiolinks.kitabu.screens.reservationDashboardScreen

@Composable
fun AppRoot() {

    val navController = rememberNavController()

    val onNavigateToLogin = { navController.navigateToLogin() }
    val onNavigateToCatalog = {  navController.navigateToCatalog() }
    val onNavigateToRegister = { navController.navigateToRegister() }
    val onNavigateToDashboard = { navController.navigateToDashboard() }

    NavHost(

        navController = navController,
        startDestination = LoginDestination

    ) {

        loginScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToRegister = onNavigateToRegister

        )

        catalogScreen(

            onNavigateToLogin = onNavigateToLogin,
            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToDashboard = onNavigateToDashboard

        )

        registerScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToLogin = onNavigateToLogin

        )

        reservationDashboardScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToLogin = onNavigateToLogin

        )

    }

}
