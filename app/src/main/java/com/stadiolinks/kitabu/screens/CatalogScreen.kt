package com.stadiolinks.kitabu.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.BookingEntity
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.sql.Date
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    var showAddBookSheet by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var authorInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("") }

    val books by authViewModel.bookList.collectAsStateWithLifecycle()

    val currentDate = LocalDate.now()
    val twoWeeksLater = currentDate.plusWeeks(2)

    // Variable that stores the search query
    var query by rememberSaveable { mutableStateOf("") }
    // Variable that stores the filtered books based on the search query
    val filteredBooks = remember(query, books) {
        if (query.isBlank()) {
            books
        } else {
            books.filter { book ->
                // Filters search results based on the book's title, author and category
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
                    onClick = { authViewModel.logout() },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                    label = { Text("Logout") }
                )

            }

        },
        floatingActionButton = {

            FloatingActionButton(

                onClick = { showAddBookSheet = true },
                containerColor = Color(0xff0B3954),
                contentColor = Color.White,
                shape = RoundedCornerShape(50.dp)

            ) {

                Icon(

                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                    tint = Color.White

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
                            isAvailable = book.isAvailable,
                            onClick = { selectedBook = book }

                        )

                    }

                }

            }

            // Modal bottom sheet that displays book details with button to reserve the book if available
            selectedBook?.let { book ->

                ModalBottomSheet(

                    onDismissRequest = { selectedBook = null }

                ) {

                    Column(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)

                    ) {

                        Column(

                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {

                            Text(

                                text = "Book Reservation",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xffFF6663)

                            )

                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = book.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Author: ${book.author}", fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Category: ${book.category}", fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(

                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {

                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Start date: ")
                                    }
                                    append(currentDate.toString())
                                }
                            )

                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Return date: ")
                                    }
                                    append(twoWeeksLater.toString())
                                }
                            )

                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(

                            onClick = {

                                val newBooking = BookingEntity(
                                    bookOwnerID = currentUser?.userID ?: 0,
                                    userName = currentUser?.userName ?: "",
                                    bookID = book.bookID,
                                    bookingDate = currentDate.toString(),
                                    returnDeadline = twoWeeksLater.toString()
                                )

                                authViewModel.addBooking(newBooking, book)
                                selectedBook = null

                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = book.isAvailable,
                            colors = ButtonDefaults.buttonColors(

                                containerColor = Color(0xff0B3954)

                            )

                        ) {

                            Text(

                                if (book.isAvailable) "Reserve Book" else "Currently Unavailable"

                            )

                        }

                    }

                }

            }

            // Modal bottom sheet that provides a form to add a new book
            if (showAddBookSheet) {

                ModalBottomSheet(

                    onDismissRequest = {

                        showAddBookSheet = false
                        titleInput = ""
                        authorInput = ""
                        categoryInput = ""

                    }

                ) {

                    Column(

                        modifier = Modifier.fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {

                        Text(

                            text = "Add New Book",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xff0B3954)

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(

                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(50.dp)

                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(

                            value = authorInput,
                            onValueChange = { authorInput = it },
                            label = { Text("Author") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(50.dp)

                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(

                            value = categoryInput,
                            onValueChange = { categoryInput = it },
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(50.dp)

                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Submit Button
                        Button(

                            onClick = {

                                if (titleInput.isNotBlank() && authorInput.isNotBlank()) {

                                    val newBook = BookEntity(

                                        title = titleInput.trim(),
                                        author = authorInput.trim(),
                                        category = categoryInput.trim().ifEmpty { "General" }

                                    )

                                    authViewModel.addBook(newBook)

                                    // Close sheet and clear fields
                                    showAddBookSheet = false
                                    titleInput = ""
                                    authorInput = ""
                                    categoryInput = ""

                                }

                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = titleInput.isNotBlank() && authorInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0B3954))

                        ) {

                            Text("Add Book")

                        }

                    }

                }

            }

        }

    }

}

// Composable that serves as the template for the book item card
@Composable
fun BookItemCard(

    title: String,
    author: String,
    category: String,
    isAvailable: Boolean,
    onClick: () -> Unit

) {

    Card(

        onClick = onClick,
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

            val badgeColor = if (isAvailable) Color(0x802E7D32) else Color(0x80C62828)

            Text(

                text = if (isAvailable) "Available" else "Borrowed",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(shape = RoundedCornerShape(25.dp), color = badgeColor)
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
