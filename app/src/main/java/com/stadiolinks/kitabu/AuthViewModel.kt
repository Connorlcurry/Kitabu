package com.stadiolinks.kitabu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import com.stadiolinks.kitabu.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(

    private val repository: UserRepository

): ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isUserLoggedIn: Boolean get() = repository.isLoggedIn()

    // Triggers the register function
    fun register(user: UserEntity) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading
            repository.registerUser(user)
                .onSuccess { _authState.value = AuthState.RegisterSuccess }
                .onFailure { exception -> _authState.value = AuthState.Error(exception.message ?: "Registration failed.") }

        }

    }

    // Triggers the login function
    fun login(email: String, password: String) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading
            repository.loginUser(email, password)
                .onSuccess { user -> _authState.value = AuthState.LoginSuccess(user.email) }
                .onFailure { exception -> _authState.value = AuthState.Error(exception.message ?: "Login failed.") }

        }

    }

    // Triggers the logout function
    fun logout() {

        viewModelScope.launch {

            _authState.value = AuthState.Loading
            repository.logoutUser()
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

// AuthViewModelFactory class that creates an instance of AuthViewModel (because AuthViewModel is passed via a constructor)
class AuthViewModelFactory(

    private val repository: UserRepository

): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }

}