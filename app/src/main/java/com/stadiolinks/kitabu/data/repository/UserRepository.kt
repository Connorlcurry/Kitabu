package com.stadiolinks.kitabu.data.repository

import android.content.Context
import com.stadiolinks.kitabu.AuthState
import com.stadiolinks.kitabu.data.database.dao.LibraryDao
import com.stadiolinks.kitabu.data.database.dao.UserDao
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserRepository(

    private val userDao: UserDao,
    context: Context

) {

    // Initializes shared preferences
    private val sharedPref = context.applicationContext.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Checks if user session is currently active
    fun isLoggedIn(): Boolean {

        return sharedPref.getBoolean("is_logged_in", false)

    }

    // Register function
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

    // Login function
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

    // Logout function
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

    // Function that retrieves the current user
    suspend fun getCurrentUser(): UserEntity? {

        val email = sharedPref.getString("logged_in_email", null) ?: return null
        return userDao.getUserByEmail(email)

    }

}