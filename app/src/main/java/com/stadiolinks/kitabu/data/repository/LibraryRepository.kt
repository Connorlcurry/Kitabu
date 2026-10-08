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

class LibraryRepository(

    private val libraryDao: LibraryDao? = null,
    context: Context

) {

    // Function that retrieves all books
    fun getAllBooks(): Flow<List<BookEntity>> {

        return libraryDao?.getAllBooks() ?: flowOf(emptyList())

    }

}