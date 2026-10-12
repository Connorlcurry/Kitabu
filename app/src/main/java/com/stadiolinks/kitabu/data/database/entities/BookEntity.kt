package com.stadiolinks.kitabu.data.database.entities

import androidx.room.ColumnInfo
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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)             // Stores the image as a byte array
    val coverImage: ByteArray? = null,
    val isAvailable: Boolean = true

) {

    override fun equals(other: Any?): Boolean {             // Overriding equals method and hashCode method
                                                            // to compare two BookEntity objects.
        if (this === other) return true                     // This checks if the reference to the
        if (javaClass != other?.javaClass) return false     // object is the same in both instances, ensuring that
                                                            // the current SQLite schema matches the pre-populated
        other as BookEntity                                 // database.

        if (bookID != other.bookID) return false
        if (isAvailable != other.isAvailable) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (category != other.category) return false
        if (coverImage != null) {                                               // If the cover image is not null, check if it is equal to the other object's cover image.

            if (other.coverImage == null) return false                          // check if the other object also has a cover image.
            if (!coverImage.contentEquals(other.coverImage)) return false       // If the cover images are not equal, return false.

        } else if (other.coverImage != null) return false

        return true

    }

    // Hash code function that generates a hash code for the object.
    override fun hashCode(): Int {

        var result = bookID
        result = 31 * result + isAvailable.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + (coverImage?.contentHashCode() ?: 0)     // If the cover image is not null, generate a hash code for it.
        return result

    }

}
