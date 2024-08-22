package com.example.project2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project2.dataClasses.SavedLocation
import kotlinx.coroutines.CoroutineScope

@Database(entities = [SavedLocation::class], version = 6, exportSchema = false)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun getSavedDao(): SavedLocationDao

    companion object {
        @Volatile
        private var INSTANCE: SavedDatabase? = null

        fun getDatabase(context: Context): SavedDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedDatabase::class.java,
                    "saved_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


