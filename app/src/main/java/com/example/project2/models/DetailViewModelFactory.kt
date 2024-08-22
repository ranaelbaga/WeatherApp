package com.example.project2.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project2.repo.SavedLocationRepository

class DetailViewModelFactory(
    private val savedLocationRepository: SavedLocationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(savedLocationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
