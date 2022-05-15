package ru.studymushrooms.repository

import ru.studymushrooms.api.TokenResponse
import ru.studymushrooms.api.UserModel

interface AuthorizationRepository {
    suspend fun register(email: String, username: String, password: String): TokenResponse

    suspend fun getUserInfo(token: String): UserModel

    suspend fun login(email: String?, username: String, password: String): TokenResponse
}