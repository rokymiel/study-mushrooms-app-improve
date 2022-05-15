package ru.studymushrooms.service_locator

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.data.*
import ru.studymushrooms.repository.*

private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

object ServiceLocator {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setDateFormat(DATE_FORMAT).create()
                )
            )
            .build()
    }

    private val serverApi by lazy {
        retrofit.create(ServerApi::class.java)
    }

    val authorizationRepository: AuthorizationRepository by lazy {
        AuthorizationRepositoryImpl(serverApi)
    }

    val catalogRepository: CatalogRepository by lazy {
        CatalogRepositoryImpl(serverApi)
    }

    val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(serverApi)
    }

    val placesRepository: PlacesRepository by lazy {
        PlacesRepositoryImpl(serverApi)
    }

    val recognitionRepository: RecognitionRepository by lazy {
        RecognitionRepositoryImpl(serverApi)
    }

    val baseUrl: String = "http://82.146.49.54:8000"

    var token: String? = null
}