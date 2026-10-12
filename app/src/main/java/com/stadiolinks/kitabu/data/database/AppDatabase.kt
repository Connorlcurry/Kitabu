package com.stadiolinks.kitabu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stadiolinks.kitabu.data.database.dao.LibraryDao
import com.stadiolinks.kitabu.data.database.dao.UserDao
import com.stadiolinks.kitabu.data.database.entities.Converters
import com.stadiolinks.kitabu.data.database.entities.UserEntity
import com.stadiolinks.kitabu.data.database.entities.BookEntity
import com.stadiolinks.kitabu.data.database.entities.BookingEntity

@Database(entities = [

    UserEntity::class,
    BookEntity::class,
    BookingEntity::class

], version = 2, exportSchema = false)
@TypeConverters(Converters::class) // Type converters for the database
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun libraryDao(): LibraryDao

    // Singleton pattern to ensure only one instance of the database is created
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(

                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_database"

                ).createFromAsset("kitabu.db")      // Pre-populated database
                    .fallbackToDestructiveMigration()               // Migration strategy to destroy and rebuild the database on version change
                    .build()

                INSTANCE = instance
                instance

            }

        }

    }

}
