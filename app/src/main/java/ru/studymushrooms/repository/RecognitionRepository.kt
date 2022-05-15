package ru.studymushrooms.repository

import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.api.RecognitionModel

interface RecognitionRepository {
    suspend fun recognize(token: String, b64Image: String): List<RecognitionModel>

    suspend fun addPlace(token: String, placeModel: PlaceModel)
}