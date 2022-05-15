package ru.studymushrooms.data

import ru.studymushrooms.api.MushroomModel
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val serverApi: ServerApi,
): CatalogRepository {
    override suspend fun getMushrooms(token: String, limit: Int?, offset: Int?): List<MushroomModel> {
        return serverApi.getMushrooms(token, limit, offset)
    }
}