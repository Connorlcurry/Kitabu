package com.stadiolinks.kitabu.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun NavigationDrawerContent(

    onNavigateToLogin: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onCloseDrawer: () -> Unit

) {

    ModalDrawerSheet {

        Column(

            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)

        ) {

            Text(

                text = "Kitabu",
                modifier = Modifier.padding(16.dp),
                color = Color(0xff1f6f4a),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold

            )

            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            NavigationDrawerItem(

                label = { Text(text = "Catalog") },
                selected = false,
                onClick = {
                    onNavigateToCatalog()
                    onCloseDrawer()
                },

                icon = {

                    Icon(Icons.Default.Home, contentDescription = "Catalog")

                }

            )
            NavigationDrawerItem(

                label = { Text(text = "Dashboard") },
                selected = false,
                onClick = {
                    onNavigateToDashboard()
                    onCloseDrawer()
                },

                icon = {

                    Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")

                }

            )
            NavigationDrawerItem(

                label = { Text(text = "Login") },
                selected = false,
                onClick = {
                    onNavigateToLogin()
                    onCloseDrawer()
                },

                icon = {

                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = "Login")

                }

            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()

            NavigationDrawerItem(

                label = { Text(text = "Log Out", color = Color.Red) },
                selected = false,
                onClick = {
                    onNavigateToLogin()
                    onCloseDrawer()
                },

                icon = {

                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)

                }

            )

        }

    }

}
