package com.example.project2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project2.dataClasses.SavedLocation

@Dao
interface SavedLocationDao {
    @Query("SELECT * FROM saved_locations")
    fun getAllSavedLocations(): LiveData<List<SavedLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: SavedLocation)

    @Query("DELETE FROM saved_locations WHERE id = :locationId")
    suspend fun deleteLocationById(locationId: Int):Int

    @Query("SELECT * FROM saved_locations WHERE name = :name LIMIT 1")
    suspend fun getLocationByName(name: String): SavedLocation?


}
