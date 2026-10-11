package com.stadiolinks.kitabu.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.stadiolinks.kitabu.data.database.entities.ActiveBookingEntity
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>> // Retrieves all books from the database

    @Query("SELECT * FROM books WHERE isAvailable = true")
    fun getAvailableBooks(): Flow<List<BookEntity>> // Retrieves all available books from the database

    @Query("SELECT * FROM bookings WHERE status IN (:status)")
    fun getBookingsByStatus(status: List<String>): Flow<List<ActiveBookingEntity>> // Retrieves bookings by a particular status from the database

    @Query("SELECT JULIANDAY(returnDeadline) - JULIANDAY(bookingDate) AS daysBetween FROM bookings WHERE bookingID = :bookingID")
    suspend fun getDaysBetween(bookingID: Int): Int // Calculates the number of days between the booking date and the return deadline

    @Insert
    suspend fun addBook(book: BookEntity) // Inserts a new book into the database

    @Insert
    suspend fun insertBooking(booking: BookingEntity) // Inserts a new booking into the database

    @Update
    suspend fun updateBookAvailability(book: BookEntity) // Updates the availability of a book when it is booked

    // Transaction that inserts a new booking and updates the availability of the book
    @Transaction
    suspend fun addBooking(booking: BookingEntity, book: BookEntity) {
        
        insertBooking(booking)
        updateBookAvailability(book.copy(isAvailable = false))

    }

    @Query("UPDATE bookings SET returnDeadline = DATE(returnDeadline, '+14 days') WHERE bookingID = :bookingID")
    suspend fun updateBookingPeriod(bookingID: Int) // Updates the booking period of an existing booking

    // Transaction that updates the booking period and updates the availability of the book
    @Transaction
    suspend fun extendBookingPeriod(book: BookEntity, bookingID: Int) {

        updateBookingPeriod(bookingID)

    }

    @Query("UPDATE bookings SET status = 'ACTIVE' WHERE bookingID = :bookingID")
    suspend fun updateStatusToActive(bookingID: Int)

    @Query("UPDATE books SET isAvailable = true WHERE bookID = :bookID")
    suspend fun updateBookToAvailable(bookID: Int)

    @Query("UPDATE bookings SET status = 'RETURNED' WHERE bookingID = :bookingID")
    suspend fun updateStatusToReturned(bookingID: Int)


    // Transaction that updates book availability and booking status when a book is returned
    @Transaction
    suspend fun returnBook(bookingID: Int, bookID: Int) {

        updateBookToAvailable(bookID)
        updateStatusToReturned(bookingID)

    }

    @Query("DELETE FROM bookings WHERE bookingID = :bookingID")
    suspend fun deleteBooking(bookingID: Int)

    // Transaction that cancels a pending booking and restores book availability
    @Transaction
    suspend fun cancelPendingBooking(bookingID: Int, bookID: Int) {

        updateBookToAvailable(bookID)
        deleteBooking(bookingID)

    }

}
