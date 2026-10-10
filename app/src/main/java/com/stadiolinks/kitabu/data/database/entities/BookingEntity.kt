package com.stadiolinks.kitabu.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class Status {

    PENDING, ACTIVE, RETURNED

}

// Converter class for the Status enum
class Converters {

    @TypeConverter
    fun fromStatus(status: Status): String = status.name

    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)

}

@Entity(

    tableName = "bookings",

    // Foreign keys for the bookings table
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["bookOwnerID"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookID"],
            childColumns = ["bookID"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],

    // Indices for foreign keys to improve database query performance
    indices = [
        Index(value = ["bookOwnerID"]),
        Index(value = ["bookID"])
    ]

)
data class BookingEntity(

    @PrimaryKey(autoGenerate = true)
    val bookingID: Int = 0,

    // Foreign keys
    val bookOwnerID: Int,
    val userName: String,
    val bookID: Int,

    // Booking details
    val bookingDate: String,
    val returnDeadline: String,
    val status: Status = Status.PENDING

)
