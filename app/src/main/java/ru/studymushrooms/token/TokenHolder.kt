package ru.studymushrooms.token

interface TokenHolder {
    fun getToken(): String

    fun setToken(token: String)
}