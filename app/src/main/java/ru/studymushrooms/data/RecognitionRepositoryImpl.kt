package ru.studymushrooms.data

import ru.studymushrooms.api.ImageRequest
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.api.RecognitionModel
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.data.mappers.MushroomMapper
import ru.studymushrooms.repository.RecognitionRepository

class RecognitionRepositoryImpl(
    private val serverApi: ServerApi,
    private val mushroomMapper: MushroomMapper,
): RecognitionRepository {
    override suspend fun recognize(token: String, b64Image: String): List<RecognitionModel> {
        return serverApi.recognize(token, ImageRequest(b64Image)).map {
            val mushroom = mushroomMapper.mapMushroom(it.mushroom)
            it.copy(mushroom = mushroom)
        }
    }

    override suspend fun addPlace(token: String, placeModel: PlaceModel) {
        serverApi.addPlace(token, placeModel)
    }
}