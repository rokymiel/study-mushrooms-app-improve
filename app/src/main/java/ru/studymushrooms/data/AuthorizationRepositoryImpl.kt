package ru.studymushrooms.data

import ru.studymushrooms.api.LoginModel
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.api.TokenResponse
import ru.studymushrooms.api.UserModel
import ru.studymushrooms.repository.AuthorizationRepository

class AuthorizationRepositoryImpl(
    private val serverApi: ServerApi,
): AuthorizationRepository {
    override suspend fun register(email: String, username: String, password: String): TokenResponse {
        val loginModel = LoginModel(email, username, password)
        return serverApi.register(loginModel)
    }

    override suspend fun getUserInfo(token: String): UserModel {
        return serverApi.getUserInfo(token)
    }

    override suspend fun login(email: String?, username: String, password: String): TokenResponse {
        val loginModel = LoginModel(email, username, password)
        return serverApi.login(loginModel)
    }
}