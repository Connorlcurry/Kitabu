package com.stadiolinks.kitabu.data.repository

import android.content.Context
import com.stadiolinks.kitabu.data.database.dao.LibraryDao
import com.stadiolinks.kitabu.data.database.entities.ActiveBookingEntity
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.BookingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class LibraryRepository(

    private val libraryDao: LibraryDao? = null,
    context: Context? = null

) {

    // Function that retrieves all books
    fun getAllBooks(): Flow<List<BookEntity>> {

        return libraryDao?.getAllBooks() ?: flowOf(emptyList())

    }

    // Function that retrieves active bookings
    fun getActiveBookings(): Flow<List<ActiveBookingEntity>> {

        return libraryDao?.getActiveBookings() ?: flowOf(emptyList())

    }

    suspend fun addBooking(bookingEntity: BookingEntity, bookEntity: BookEntity) = withContext(Dispatchers.IO) {

        libraryDao?.addBooking(bookingEntity, bookEntity)

    }

    suspend fun getDaysBetween(bookingID: Int): Int = withContext(Dispatchers.IO) {

        libraryDao?.getDaysBetween(bookingID) ?: 0

    }

}
