package ru.studymushrooms.data

import ru.studymushrooms.api.ImageRequest
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.api.RecognitionModel
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.repository.RecognitionRepository

class RecognitionRepositoryImpl(
    private val serverApi: ServerApi,
): RecognitionRepository {
    override suspend fun recognize(token: String, b64Image: String): List<RecognitionModel> {
        return serverApi.recognize(token, ImageRequest(b64Image))
    }

    override suspend fun addPlace(token: String, placeModel: PlaceModel) {
        serverApi.addPlace(token, placeModel)
    }
}