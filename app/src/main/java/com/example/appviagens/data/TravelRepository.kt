package com.example.appviagens.data

class TravelRepository(private val travelDao: TravelDao) {
    suspend fun insertTravel(travel: Travel) = travelDao.insertTravel(travel)
    suspend fun getTravelsByUser(userId: Int) = travelDao.getTravelsByUser(userId)
    suspend fun deleteTravel(travelId: Int) = travelDao.deleteTravel(travelId)
}
