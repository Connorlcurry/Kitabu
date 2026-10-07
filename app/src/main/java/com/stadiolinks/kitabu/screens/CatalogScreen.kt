package com.stadiolinks.kitabu.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stadiolinks.kitabu.AuthState
import com.stadiolinks.kitabu.AuthViewModel
import com.stadiolinks.kitabu.AuthViewModelFactory
import com.stadiolinks.kitabu.components.NavigationDrawerContent
import com.stadiolinks.kitabu.data.database.AppDatabase
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(

    onNavigateToCatalog: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel

) {

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {

        when (authState) {

            is AuthState.LoggedOut -> onNavigateToLogin()
            is AuthState.Error -> Toast.makeText(
                context,
                (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit

        }

    }

    ModalNavigationDrawer(

        drawerState = drawerState,
        drawerContent = {

            NavigationDrawerContent(

                onNavigateToLogin = onNavigateToLogin,
                onNavigateToCatalog = onNavigateToCatalog,
                onNavigateToDashboard = onNavigateToDashboard,
                onCloseDrawer =  { scope.launch { drawerState.close() } }

            )

        }

    ) { }

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text("Book Catalog") }

            )

        }

    ) { paddingValues ->

        Column(

            modifier = Modifier.padding(paddingValues)

        ) {}

    }

}

@Serializable
data object CatalogDestination

fun NavGraphBuilder.catalogScreen(

    onNavigateToLogin: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToDashboard: () -> Unit

) {

    composable<CatalogDestination> {

        val context = LocalContext.current.applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = UserRepository(database.userDao(), context)
        val factory = AuthViewModelFactory(repository)
        val authViewModel: AuthViewModel = viewModel(factory = factory)

        CatalogScreen(

            onNavigateToLogin = onNavigateToLogin,
            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToDashboard = onNavigateToDashboard,
            authViewModel = authViewModel

        )

    }

}

fun NavController.navigateToCatalog() {

    navigate(CatalogDestination)

}