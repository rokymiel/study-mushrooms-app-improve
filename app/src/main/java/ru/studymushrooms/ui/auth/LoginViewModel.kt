package ru.studymushrooms.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.studymushrooms.repository.AuthorizationRepository
import ru.studymushrooms.token.TokenHolder

class LoginViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val tokenHolder: TokenHolder,
) : ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState

    init {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }


    fun authenticate(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authorizationRepository.login(null, username, password)
                }
                tokenHolder.setToken("Token " + response.token)
                _authenticationState.value = AuthenticationState.AUTHENTICATED
            } catch (t: Throwable) {
                Log.e("Retrofit", t.toString())
                _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authorizationRepository.login(email, username, password)
                }
                tokenHolder.setToken("Token " + response.token)
                _authenticationState.value = AuthenticationState.AUTHENTICATED
            } catch (t: Throwable) {
                Log.e("Retrofit", t.toString())
                _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            }
        }
    }

    fun refuseAuthentication() {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }
}
