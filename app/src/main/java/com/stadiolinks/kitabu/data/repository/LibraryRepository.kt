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

// ====================================================================================
//
// The library repository serves as a bridge between the view model and data related
// to books and reservations.
// The repository is injected with the Library data access object and the application
// context and executes the functions of the Library data access object on a background
// thread using coroutines, and then exposes the results to the AuthViewModel.
//
// ====================================================================================

class LibraryRepository(

    private val libraryDao: LibraryDao? = null,
    context: Context? = null

) {

    fun getAllBooks(): Flow<List<BookEntity>> {

        return libraryDao?.getAllBooks() ?: flowOf(emptyList())

    }

    fun getActiveAndPendingBookings(): Flow<List<ActiveBookingEntity>> {

        return libraryDao?.getBookingsByStatus(listOf("ACTIVE", "PENDING")) ?: flowOf(emptyList())

    }

    suspend fun addBook(book: BookEntity) {

        libraryDao?.addBook(book)

    }

    suspend fun addBooking(bookingEntity: BookingEntity, bookEntity: BookEntity) = withContext(Dispatchers.IO) {

        libraryDao?.addBooking(bookingEntity, bookEntity)

    }

    suspend fun getDaysBetween(bookingID: Int): Int = withContext(Dispatchers.IO) {

        libraryDao?.getDaysBetween(bookingID) ?: 0

    }

    suspend fun updateStatusToActive(bookingID: Int) = withContext(Dispatchers.IO) {

        libraryDao?.updateStatusToActive(bookingID)

    }

    suspend fun extendBookingPeriod(bookEntity: BookEntity, bookingID: Int) = withContext(Dispatchers.IO) {

        libraryDao?.extendBookingPeriod(bookEntity, bookingID)

    }

    suspend fun returnBook(bookingID: Int, bookID: Int) = withContext(Dispatchers.IO) {

        libraryDao?.returnBook(bookingID, bookID)

    }

    suspend fun cancelPendingBooking(bookingID: Int, bookID: Int) = withContext(Dispatchers.IO) {

        libraryDao?.cancelPendingBooking(bookingID, bookID)

    }

}
