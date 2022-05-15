package ru.studymushrooms.repository

import ru.studymushrooms.api.PlaceModel

interface PlacesRepository {
    suspend fun getPlaces(token: String): List<PlaceModel>
}