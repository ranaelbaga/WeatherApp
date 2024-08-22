package com.example.project2.repo

import androidx.lifecycle.LiveData
import com.example.project2.dataClasses.SavedLocation
import com.example.project2.db.SavedDatabase
import com.example.project2.db.SavedLocationDao

class SavedLocationRepository(private val db: SavedDatabase) {

    private val savedLocationDao: SavedLocationDao = db.getSavedDao()

    suspend fun upsert(savedLocation: SavedLocation) = db.getSavedDao().upsert(savedLocation)

    fun getSavedLocation() = db.getSavedDao().getAllSavedLocations()

    suspend fun getSavedLocationByName(name: String): SavedLocation? {
        return savedLocationDao.getLocationByName(name)
    }

    suspend fun deleteLocationById(locationId: Int) {
        savedLocationDao.deleteLocationById(locationId)
    }


    suspend fun isLocationSaved(name: String): Boolean {
        return savedLocationDao.getLocationByName(name) != null
    }
}

