package com.stadiolinks.kitabu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import com.stadiolinks.kitabu.data.repository.UserRepository
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.repository.LibraryRepository
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