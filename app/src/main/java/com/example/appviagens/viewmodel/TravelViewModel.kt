package com.example.appviagens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appviagens.data.Travel
import com.example.appviagens.data.TravelRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TravelViewModel(private val repository: TravelRepository) : ViewModel() {
    private val apiKey = "AIzaSyBjhbhy9-4mB8sgzJ2hzuRk2ClDjRlWLS4"

    fun insertTravel(travel: Travel) {
        viewModelScope.launch {
            repository.insertTravel(travel)
        }
    }

    fun updateTravel(travel: Travel) {
        viewModelScope.launch {
            repository.updateTravel(travel)
        }
    }

    fun deleteTravel(travelId: Int) {
        viewModelScope.launch {
            repository.deleteTravel(travelId)
        }
    }

    suspend fun getTravelsByUser(userId: Int): List<Travel> {
        return repository.getTravelsByUser(userId)
    }

    suspend fun getTravelById(travelId: Int): Travel? {
        return repository.getTravelById(travelId)
    }

    suspend fun gerarRoteiroGemini(travel: Travel): String = withContext(Dispatchers.IO) {
        try {
            val generativeModel = GenerativeModel(
                modelName = "models/gemini-1.5-pro-latest",
                apiKey = apiKey
            )

            val promptText = """
                Crie um roteiro de viagem para o destino: ${travel.destination}.
                Tipo de viagem: ${travel.travelType}.
                Data: de ${travel.startDate} até ${travel.endDate}.
                Orçamento disponível: R$${travel.budget}.
                Dê sugestões realistas, em português, considerando o perfil da viagem.
            """.trimIndent()

            val response = generativeModel.generateContent(content { text(promptText) })
            return@withContext response.text ?: "Não foi possível gerar o roteiro no momento."
        } catch (e: Exception) {
            Log.e("TravelViewModel", "Erro ao gerar roteiro", e)
            return@withContext "Erro ao gerar roteiro: ${e.message ?: "erro desconhecido"}"
        }
    }
}
