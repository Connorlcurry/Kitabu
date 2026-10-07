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

], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun libraryDao(): LibraryDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(

                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_database"

                ).createFromAsset("kitabu.db")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance

            }

        }

    }

}