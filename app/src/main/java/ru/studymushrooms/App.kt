package ru.studymushrooms

import android.app.Application
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.service_locator.ServiceLocator.baseUrl

private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setDateFormat(DATE_FORMAT).create()
                )
            ).build()
        api = retrofit.create(ServerApi::class.java)
    }

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var api: ServerApi
        var token: String? = null
    }

}