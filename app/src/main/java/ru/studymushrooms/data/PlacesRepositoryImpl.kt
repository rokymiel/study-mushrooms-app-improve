package ru.studymushrooms.data

import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.repository.PlacesRepository

class PlacesRepositoryImpl(
    private val serverApi: ServerApi
): PlacesRepository {
    override suspend fun getPlaces(token: String): List<PlaceModel> {
        return serverApi.getPlaces(token)
    }
}