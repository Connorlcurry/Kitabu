package com.stadiolinks.kitabu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity (

    @PrimaryKey(autoGenerate = true)
    val bookID: Int = 0,

    // Book details
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean = true

)