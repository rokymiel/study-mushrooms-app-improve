package ru.studymushrooms.service_locator

import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.studymushrooms.BuildConfig
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.data.*
import ru.studymushrooms.data.mappers.MushroomMapper
import ru.studymushrooms.repository.*
import ru.studymushrooms.token.TokenHolder
import ru.studymushrooms.token.TokenHolderImpl
import ru.studymushrooms.ui.auth.LoginViewModel
import ru.studymushrooms.ui.auth.LoginViewModelFactory
import ru.studymushrooms.ui.catalog.CatalogViewModel
import ru.studymushrooms.ui.catalog.CatalogViewModelFactory
import ru.studymushrooms.ui.maps.MapsViewModel
import ru.studymushrooms.ui.maps.MapsViewModelFactory
import ru.studymushrooms.ui.notes.NotesViewModel
import ru.studymushrooms.ui.notes.NotesViewModelFactory
import ru.studymushrooms.ui.recognize.RecognitionViewModel
import ru.studymushrooms.ui.recognize.RecognitionViewModelFactory

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
        CatalogRepositoryImpl(serverApi, MushroomMapper())
    }

    val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(serverApi)
    }

    val placesRepository: PlacesRepository by lazy {
        PlacesRepositoryImpl(serverApi, baseUrl)
    }

    val recognitionRepository: RecognitionRepository by lazy {
        RecognitionRepositoryImpl(serverApi, MushroomMapper())
    }

    val tokenHolder: TokenHolder by lazy { TokenHolderImpl() }

    private const val baseUrl: String = "http://82.146.49.54:8000"

    private val viewModelFactories = mapOf(
        LoginViewModel::class.java to LoginViewModelFactory(),
        CatalogViewModel::class.java to CatalogViewModelFactory(),
        MapsViewModel::class.java to MapsViewModelFactory(),
        NotesViewModel::class.java to NotesViewModelFactory(),
        RecognitionViewModel::class.java to RecognitionViewModelFactory(),
    )

    val viewModelFactory: ViewModelProvider.Factory by lazy { ViewModelCreator(viewModelFactories) }
}