package com.stadiolinks.kitabu.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.stadiolinks.kitabu.components.NavigationDrawerContent
import com.stadiolinks.kitabu.data.database.AppDatabase
import com.stadiolinks.kitabu.data.repository.LibraryRepository
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

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val books by authViewModel.bookList.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    val filteredBooks = remember(query, books) {
        if (query.isBlank()) {
            books
        } else {
            books.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true) ||
                book.category.contains(query, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {

        authViewModel.loadCurrentUser()

    }

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

                title = {

                    Text(

                        text = "Hi, ${currentUser?.userName ?: "Guest"}!",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold

                    )

                }

            )

        },
        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateToCatalog,
                    icon = { Icon(Icons.Default.Book, contentDescription = "Catalog", tint = Color(0xff0B3954)) },
                    label = { Text("Catalog") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToDashboard,
                    icon = { Icon(Icons.Default.AddChart, contentDescription = "Reservation Dashboard") },
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

            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)

        ) {

            // Search Bar
            OutlinedTextField(

                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search for a book...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Icon"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0B3954),
                    unfocusedBorderColor = Color.LightGray
                )

            )

            // Lazy column that displays filtered books
            LazyColumn(

                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                if (filteredBooks.isEmpty()) {

                    item {

                        Text(

                            text = if (query.isBlank()) "No books available." else "No books found matching \"$query\".",
                            modifier = Modifier.padding(16.dp),
                            color = Color.DarkGray

                        )

                    }

                } else {

                    items(

                        items = filteredBooks,
                        key = { book -> book.bookID }

                    ) { book ->

                        BookItemCard(

                            title = book.title,
                            author = book.author,
                            category = book.category,
                            isAvailable = book.isAvailable

                        )

                    }

                }

            }

        }

    }

}

// Composable that serves as the template for the book item card
@Composable
fun BookItemCard(title: String, author: String, category: String, isAvailable: Boolean) {

    Card(

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        ),
        modifier = Modifier.fillMaxWidth()

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Author: $author", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Category: $category", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = if (isAvailable) "Available" else "Borrowed",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = if
                        (isAvailable) Modifier.background(shape = RoundedCornerShape(25.dp), color = Color(0x802E7D32))
                    .padding(8.dp)
                else
                    Modifier.background(shape = RoundedCornerShape(25.dp), color = Color(0x80C62828))
                    .padding(8.dp)

            )

        }

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
        val userRepository = UserRepository(database.userDao(), context)
        val libraryRepository = LibraryRepository(database.libraryDao(), context)
        val factory = AuthViewModelFactory(userRepository, libraryRepository)
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
