package com.stadiolinks.kitabu.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val userID: Int = 0,

    // Personal details
    val userName: String,
    val email: String,
    val password: String,

    // Book renting details
    val currentBooksRented: Int = 0,
    val outstandingFines: Double = 0.0

)