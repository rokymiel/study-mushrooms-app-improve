package ru.studymushrooms.token

class TokenHolderImpl: TokenHolder {
    private var token: String? = null

    override fun getToken(): String {
        return requireNotNull(token)
    }

    override fun setToken(token: String) {
        this.token = token
    }
}