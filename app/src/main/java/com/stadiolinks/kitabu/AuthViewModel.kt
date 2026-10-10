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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(

    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository

): ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isUserLoggedIn: Boolean get() = userRepository.isLoggedIn()
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val bookList: StateFlow<List<BookEntity>> = libraryRepository.getAllBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeBookings: StateFlow<List<ActiveBookingEntity>> = libraryRepository.getActiveBookings()
        .stateIn(
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

    // Triggers the deleteUser function
    fun deleteUser(userID: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            try {

                _authState.value = AuthState.Loading
                userRepository.deleteUser(userID)
                _authState.value = AuthState.Idle

            } catch (e: Exception) {

                _authState.value = AuthState.Error(e.message ?: "Failed to delete user")

            }

        }

    }

    // Adds a new booking and updates the relevant book's availability
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

    // Resets the authentication state
    fun resetState() {

        _authState.value = AuthState.Idle

    }

}

// AuthState sealed class that contains all states of the authentication process
sealed class AuthState {

    object Idle : AuthState()
    object Loading : AuthState()
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

    override fun <T: ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository, libraryRepository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }

}