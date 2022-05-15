package ru.studymushrooms.repository

import ru.studymushrooms.api.MushroomModel

interface CatalogRepository {
    suspend fun getMushrooms(token: String, limit: Int?, offset: Int?): List<MushroomModel>
}