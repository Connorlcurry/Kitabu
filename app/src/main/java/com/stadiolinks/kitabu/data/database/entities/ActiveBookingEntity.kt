package com.stadiolinks.kitabu.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ActiveBookingEntity(

    @Embedded val booking: BookingEntity, // The BookingEntity object representing the booking

    @Relation(

        parentColumn = "bookID", // The column in the BookingEntity table that references the bookID
        entityColumn = "bookID" // The column in the BookEntity table that is the primary key

    )

    val book: BookEntity // The BookEntity object representing the book associated with the booking

)