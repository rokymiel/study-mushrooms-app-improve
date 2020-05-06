package ru.studymushrooms

import android.app.Application
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.studymushrooms.api.ServerApi

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
                )
            ).build()
        api = retrofit.create(ServerApi::class.java)
    }

    companion object {
        val baseUrl: String = "http://82.146.49.54:8000"
        lateinit var retrofit: Retrofit
        lateinit var api: ServerApi
        var token: String? = null
    }

}