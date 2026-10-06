package com.stadiolinks.kitabu.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onNavigateToCatalog: (String) -> Unit, onNavigateToDashboard: (String) -> Unit) {

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

    onNavigateToCatalog: (String) -> Unit,
    onNavigateToDashboard: (String) -> Unit

) {

    composable<CatalogDestination> {

        CatalogScreen(

            onNavigateToCatalog = onNavigateToCatalog,
            onNavigateToDashboard = onNavigateToDashboard

        )

    }

}

fun NavController.navigateToCatalog() {

    navigate(CatalogDestination)

}