package com.stadiolinks.kitabu.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.sql.Timestamp

enum class Status {

    PENDING, ACTIVE, RETURNED

}

// Converter class for the Status enum and Timestamp type
class Converters {

    @TypeConverter
    fun fromStatus(status: Status): String = status.name

    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)

    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? = value?.time

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? = value?.let { Timestamp(it) }

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
    val bookingDate: Timestamp,
    val returnDeadline: Timestamp,
    val status: Status = Status.PENDING

)