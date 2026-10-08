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

    @Query("SELECT * FROM books WHERE title LIKE '%' || :searchQuery || '%'")
    fun searchBooks(searchQuery: String): Flow<List<BookEntity>> // Searches for books by title

    @Transaction
    @Query("SELECT * FROM bookings WHERE status = 'ACTIVE'")
    suspend fun getActiveBookings(): List<ActiveBookingEntity> // Retrieves all active bookings from the database

    @Insert
    suspend fun addBook(book: BookEntity) // Inserts a new book into the database

    @Insert
    suspend fun addBooking(booking: BookingEntity) // Inserts a new booking into the database

    @Update
    suspend fun extendBookingPeriod(booking: BookingEntity) // Updates the booking period of an existing booking

    @Update
    suspend fun returnBook(book: BookEntity) // Updates the availability of a book when it is returned

    @Delete
    suspend fun cancelPendingBooking(booking: BookingEntity) // Deletes a pending booking from the database

}