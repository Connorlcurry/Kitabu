package com.stadiolinks.kitabu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userID = :userID LIMIT 1")
    fun getCurrentUser(userID: Int): Flow<UserEntity?> // Fetches the current user by ID

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity? // Fetches a specific record from the users table by email

    @Insert
    suspend fun registerUser(user: UserEntity) // Inserts a new record into the users table

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun loginUser(email: String, password: String): UserEntity? // Function used to log the user in

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email = :email)")
    suspend fun isEmailTaken(email: String): Boolean // Checks if an email is already registered

}