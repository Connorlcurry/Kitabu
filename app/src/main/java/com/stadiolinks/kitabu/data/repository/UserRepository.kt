package com.stadiolinks.kitabu.data.repository

import android.content.Context
import com.stadiolinks.kitabu.data.database.dao.UserDao
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ====================================================================================
//
// The user repository serves as the abstract mediator between the view model and user data.
// The repository is injected with the User data access object and the application
// context and executes the functions of the User data access object on a background
// thread using coroutines, and then exposes the results to the AuthViewModel.
//
// ====================================================================================

class UserRepository(

    private val userDao: UserDao,
    context: Context

) {

    // Initializes shared preferences for the current user session
    private val sharedPref = context.applicationContext
        .getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Checks if user session is currently active
    fun isLoggedIn(): Boolean {

        return sharedPref.getBoolean("is_logged_in", false)

    }

    // Logic for the register function
    suspend fun registerUser(

        user: UserEntity

    ): Result<Unit> = withContext(Dispatchers.IO) {

        // Checks if email is already registered before registering the user
        return@withContext try {

            if (userDao.isEmailTaken(user.email)) {

                Result.failure(Exception("Email is already registered."))

            } else {

                userDao.registerUser(user)
                Result.success(Unit)

            }

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    // Logic for the login function
    suspend fun loginUser(

        email: String,
        password: String

    ): Result<UserEntity> = withContext(Dispatchers.IO) {

        return@withContext try {

            val user = userDao.loginUser(email, password)

            if (user != null) {

                // Saves session data to shared preferences when user verification is successful
                sharedPref.edit().apply {

                    putBoolean("is_logged_in", true)
                    putString("logged_in_email", email)
                    apply()

                }

                Result.success(user)

            } else {

                Result.failure(Exception("Invalid email or password."))

            }

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    // Logic for the logout function
    suspend fun logoutUser(): Result<Unit> = withContext(Dispatchers.IO) {

        return@withContext try {

            // Clears session data from shared preferences
            sharedPref.edit().apply {

                putBoolean("is_logged_in", false)
                putString("logged_in_email", null)
                apply()

            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    // Logic for the function that retrieves the current user
    suspend fun getCurrentUser(): UserEntity? {

        val email = sharedPref.getString("logged_in_email", null) ?: return null
        return userDao.getUserByEmail(email)

    }

}