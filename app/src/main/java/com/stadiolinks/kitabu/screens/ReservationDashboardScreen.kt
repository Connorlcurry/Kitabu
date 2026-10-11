package com.stadiolinks.kitabu.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.BookingEntity
import com.stadiolinks.kitabu.data.database.entities.Status
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

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val activeBookings by authViewModel.activeBookings.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {

        when (authState) {

            is AuthState.LoggedOut -> onNavigateToLogin()
            is AuthState.Error -> Toast.makeText(
                context,
                (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit

        }

    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text = "Dashboard",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold

                    )

                }

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
                    onClick = { authViewModel.logout() },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                    label = { Text("Logout") }
                )

            }

        }

    ) { paddingValues ->

        if (activeBookings.isEmpty()) {

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center

            ) {

                Text(text = "No active reservations found.", fontSize = 16.sp, color = Color.Gray)

            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                items(activeBookings) { activeBooking ->

                    Card(

                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)

                    ) {

                        Column(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)

                        ) {

                            Text(
                                text = activeBooking.book.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xff0B3954)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Author: ${activeBooking.book.author}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Category: ${activeBooking.book.category}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Reserved by: ${activeBooking.booking.userName}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Booking Date: ${activeBooking.booking.bookingDate}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Return Deadline: ${activeBooking.booking.returnDeadline}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Days left: " + authViewModel.getDaysBetween(activeBooking.booking.bookingID).toString(), fontSize = 14.sp, color = Color(0xffFF6663))

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Status: ${activeBooking.booking.status}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (activeBooking.booking.status == Status.ACTIVE) {

                                Row(

                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween

                                ) {

                                    // Renew button
                                    Button(

                                        onClick = {

                                            authViewModel.extendBookingPeriod(activeBooking.book, activeBooking.booking.bookingID)

                                        },
                                        colors = ButtonDefaults.buttonColors(

                                            containerColor = Color(0xFF2E7D32)

                                        )

                                    ) {

                                        Text(

                                            text = "Renew",
                                            fontSize = 14.sp

                                        )

                                    }

                                    // Return button
                                    Button(

                                        onClick = {

                                            authViewModel.returnBook(activeBooking.booking.bookingID, activeBooking.book.bookID)

                                        },
                                        colors = ButtonDefaults.buttonColors(

                                            containerColor = Color(0xffFF6663)

                                        )

                                    ) {

                                        Text(

                                            text = "Return",
                                            fontSize = 14.sp

                                        )

                                    }

                                }

                            } else if (activeBooking.booking.status == Status.PENDING) {

                                Row(

                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween

                                ) {

                                    Button(

                                        onClick = {

                                            authViewModel.updateStatusToActive(activeBooking.booking.bookingID)

                                        },
                                        colors = ButtonDefaults.buttonColors(

                                            containerColor = Color(0xFF2E7D32)

                                        )

                                    ) {

                                        Text(

                                            text = "Confirm",
                                            fontSize = 14.sp

                                        )

                                    }

                                    // Cancel button
                                    Button(

                                        onClick = {

                                            authViewModel.cancelPendingBooking(activeBooking.booking.bookingID, activeBooking.book.bookID)

                                        },
                                        colors = ButtonDefaults.buttonColors(

                                            containerColor = Color(0xffFF6663)

                                        )

                                    ) {

                                        Text(

                                            text = "Cancel",
                                            fontSize = 14.sp

                                        )

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

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
