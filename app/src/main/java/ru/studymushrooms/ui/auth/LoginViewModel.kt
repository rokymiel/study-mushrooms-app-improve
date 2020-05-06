package ru.studymushrooms.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.LoginModel
import ru.studymushrooms.api.TokenResponse

class LoginViewModel : ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    val authenticationState = MutableLiveData<AuthenticationState>()

    var username: String

    init {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
        username = ""
    }


    fun authenticate(username: String, password: String) {
        val call = App.api.login(
            LoginModel(
                null,
                username,
                password
            )
        )

        call.enqueue(object : retrofit2.Callback<TokenResponse> {
            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("Retrofit", t.toString())
                authenticationState.value = AuthenticationState.UNAUTHENTICATED
            }

            override fun onResponse(
                call: Call<TokenResponse>,
                response: Response<TokenResponse>
            ) {
                if (response.isSuccessful) {
                    App.token = "Token " + response.body()?.token
                    authenticationState.value = AuthenticationState.AUTHENTICATED
                } else
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
            }

        })

    }

    fun register(username: String, email: String, password: String) {
        val call = App.api.register(
            LoginModel(
                email,
                username,
                password
            )
        )

        call.enqueue(object : retrofit2.Callback<TokenResponse> {
            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("Retrofit", t.toString())
                authenticationState.value = AuthenticationState.UNAUTHENTICATED
            }

            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    App.token = "Token " + response.body()?.token
                    authenticationState.value = AuthenticationState.AUTHENTICATED
                } else
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION

            }

        })
    }

    fun refuseAuthentication() {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }
}
