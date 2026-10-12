package com.stadiolinks.kitabu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import com.stadiolinks.kitabu.data.repository.UserRepository
import com.stadiolinks.kitabu.data.database.entities.ActiveBookingEntity
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.BookingEntity
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ====================================================================================
//
// The AuthViewModel serves as a mediator between the app's UI and the app's data layer.
// Instead of constantly restarting the data query from the repository, the ViewModel
// fetches data and exposes it as an observable state, which can be observed by the UI.
//
// ====================================================================================

class AuthViewModel(

    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository

): ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle) // Stores the authentication state
    val authState: StateFlow<AuthState> = _authState.asStateFlow() // Exposes the authentication state as an observable state

    val isUserLoggedIn: Boolean get() = userRepository.isLoggedIn() // Checks if the user is logged in
    private val _currentUser = MutableStateFlow<UserEntity?>(null) // Stores the current user
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow() // Exposes the current user as an observable state

    val bookList: StateFlow<List<BookEntity>> = libraryRepository.getAllBooks() // Exposes the book list as an observable state,
        .stateIn(                                                               // using the stateIn function to convert the Flow to
            scope = viewModelScope,                                             // a StateFlow
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeBookings: StateFlow<List<ActiveBookingEntity>> = libraryRepository.getActiveAndPendingBookings()  // Exposes the active and pending bookings
        .stateIn(                                                                                               // as an observable state
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calculates the number of days left for a booking
    fun getDaysBetween(bookingID: Int): Long {

        val booking = activeBookings.value.find { it.booking.bookingID == bookingID }?.booking

        return booking?.let {

            try {

                val deadline = java.time.LocalDate.parse(it.returnDeadline)
                java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), deadline)

            } catch (_: Exception) {

                0L

            }

        } ?: 0L

    }

    // Loads the current user
    fun loadCurrentUser() {

        viewModelScope.launch {

            _currentUser.value = userRepository.getCurrentUser()

        }

    }

    // Triggers the register function
    fun register(user: UserEntity) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading
            userRepository.registerUser(user)
                .onSuccess { _authState.value = AuthState.RegisterSuccess }
                .onFailure { exception -> _authState.value = AuthState.Error(exception.message ?: "Registration failed.") }

        }

    }

    // Triggers the login function
    fun login(email: String, password: String) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading
            userRepository.loginUser(email, password)
                .onSuccess { user -> _authState.value = AuthState.LoginSuccess(user.email) }
                .onFailure { exception -> _authState.value = AuthState.Error(exception.message ?: "Login failed.") }

        }

    }

    // Triggers the logout function
    fun logout() {

        viewModelScope.launch {

            _authState.value = AuthState.Loading
            userRepository.logoutUser()
                .onSuccess { _authState.value = AuthState.LoggedOut }
                .onFailure { exception -> _authState.value = AuthState.Error(exception.message ?: "Logout failed.") }

        }

    }

    // Adds a book to the library
    fun addBook(book: BookEntity) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading
                libraryRepository.addBook(book)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to add book.")

            }

        }

    }

    // Adds a booking to the reservation system
    fun addBooking(booking: BookingEntity, book: BookEntity) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading
                libraryRepository.addBooking(booking, book)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to add booking.")

            }

        }

    }

    // Updates the status of a booking to active
    fun updateStatusToActive(bookingID: Int) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading
                libraryRepository.updateStatusToActive(bookingID)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to confirm booking.")

            }

        }

    }

    // Extends the return deadline of a book
    fun extendBookingPeriod(bookEntity: BookEntity, bookingID: Int) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading
                libraryRepository.extendBookingPeriod(bookEntity, bookingID)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to renew book.")

            }

        }

    }

    // Returns a book to the library
    fun returnBook(bookingID: Int, bookID: Int) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading
                libraryRepository.returnBook(bookingID, bookID)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to return book.")

            }

        }

    }

    // Cancels a pending reservation and deletes the booking
    fun cancelPendingBooking(bookingID: Int, bookID: Int) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading
                libraryRepository.cancelPendingBooking(bookingID, bookID)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to cancel pending booking.")

            }

        }

    }

}

// AuthState sealed class that contains all states of the authentication process
sealed class AuthState {

    object Idle : AuthState() // Called when the authentication process is idle
    object Loading : AuthState() // Called when the authentication process is loading
    object RegisterSuccess : AuthState()
    data class LoginSuccess(val email: String) : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()

}

// AuthViewModelFactory class that creates an instance of AuthViewModel (AuthViewModel is passed via a constructor)
class AuthViewModelFactory(

    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository

): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {               // Creates an instance of the AuthViewModel if
                                                                                // the modelClass is assignable from AuthViewModel
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository, libraryRepository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }

}