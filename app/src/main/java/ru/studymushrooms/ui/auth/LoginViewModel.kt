package ru.studymushrooms.ui.auth

import android.text.TextUtils
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.studymushrooms.R
import ru.studymushrooms.repository.AuthorizationRepository
import ru.studymushrooms.token.TokenHolder
import ru.studymushrooms.utils.SingleLiveEvent

enum class RegisterResultMessage(@StringRes val res: Int) {
    SUCCESS(R.string.register_success),
    ERROR(R.string.register_error);
}

enum class LoginResultMessage(@StringRes val res: Int) {
    SUCCESS(R.string.register_success),
    ERROR(R.string.login_error);
}

enum class ToastEventErrorMessage(@StringRes val res: Int) {
    INVALID_EMAIL(R.string.email_error_template),
    INVALID_USERNAME(R.string.username_error_template),
    INVALID_PASSWORD(R.string.password_error_template)
}

sealed class RegisterEvents {
    data class RegisterResult(val message: RegisterResultMessage) : RegisterEvents()
}

sealed class LoginEvents {
    data class LoginResult(val message: LoginResultMessage) : LoginEvents()
}

sealed class ToastEvent {
    data class ErrorString(val errorMessage: ToastEventErrorMessage): ToastEvent()
    data class ResString(@StringRes val res: Int): ToastEvent()
}

class LoginViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val tokenHolder: TokenHolder,
) : ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    private val _showToastEvents = SingleLiveEvent<ToastEvent>()
    val showToastEvents: LiveData<ToastEvent> = _showToastEvents

    private val _registerEvents = SingleLiveEvent<RegisterEvents>()
    val registerEvents: LiveData<RegisterEvents> = _registerEvents

    private val _loginEvents = SingleLiveEvent<LoginEvents>()
    val loginEvents: LiveData<LoginEvents> = _loginEvents

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState

    init {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    private fun validEmail(email: String): Boolean {
        return TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun validUsername(name: String): Boolean {
        return name.matches("""^[A-Za-z0-9]{6,}$""".toRegex())
    }

    private fun validPassword(name: String): Boolean {
        return name.matches("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$""".toRegex())
    }

    fun authenticate(username: String, password: String) {

        if (!validUsername(username)) {
            _showToastEvents.value = ToastEvent.ErrorString(ToastEventErrorMessage.INVALID_USERNAME)
            return
        }
        if (!validPassword(password)) {
            _showToastEvents.value = ToastEvent.ErrorString(ToastEventErrorMessage.INVALID_PASSWORD)
            return
        }

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
                _loginEvents.value =
                    LoginEvents.LoginResult(LoginResultMessage.ERROR)
            }
        }
    }

    fun register(username: String, email: String, password: String) {

        if (!validEmail(email)) {
            _showToastEvents.value = ToastEvent.ErrorString(ToastEventErrorMessage.INVALID_EMAIL)
            return
        }
        if (!validUsername(username)) {
            _showToastEvents.value = ToastEvent.ErrorString(ToastEventErrorMessage.INVALID_USERNAME)
            return
        }
        if (!validPassword(password)) {
            _showToastEvents.value = ToastEvent.ErrorString(ToastEventErrorMessage.INVALID_PASSWORD)
            return
        }

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authorizationRepository.login(email, username, password)
                }
                tokenHolder.setToken("Token " + response.token)
                _authenticationState.value = AuthenticationState.AUTHENTICATED
//                _registerEvents.value =                       # зочемб? низочемб
//                    RegisterEvents.ShowRegisterResult(RegisterRequestResult.SUCCESS)
            } catch (t: Throwable) {
                Log.e("Retrofit", t.toString())
                _authenticationState.value = AuthenticationState.UNAUTHENTICATED
                _registerEvents.value =
                    RegisterEvents.RegisterResult(RegisterResultMessage.ERROR)
            }
        }
    }

    fun refuseAuthentication() {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }
}
