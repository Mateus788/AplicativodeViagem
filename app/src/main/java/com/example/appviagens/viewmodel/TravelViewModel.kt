package com.example.appviagens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appviagens.data.Travel
import com.example.appviagens.data.TravelRepository
import kotlinx.coroutines.launch

class TravelViewModel(private val repository: TravelRepository) : ViewModel() {
    fun insertTravel(travel: Travel) {
        viewModelScope.launch {
            repository.insertTravel(travel)
        }
    }

    suspend fun getTravelsByUser(userId: Int): List<Travel> {
        return repository.getTravelsByUser(userId)
    }

    fun deleteTravel(travelId: Int) {
        viewModelScope.launch {
            repository.deleteTravel(travelId)
        }
    }
}
