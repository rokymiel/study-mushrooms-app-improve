package ru.studymushrooms.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.repository.PlacesRepository

class MapsViewModel(
    private val placesRepository: PlacesRepository,
) : ViewModel() {
    private val _places = MutableLiveData<List<PlaceModel>>()
    val places: LiveData<List<PlaceModel>> = _places

    fun getPlaces() {
        viewModelScope.launch {
            try {
                val places = withContext(Dispatchers.IO) {
                    placesRepository.getPlaces(App.token!!)
                }
                _places.value = places
            } catch (t: Throwable) {
                throw t // TODO: change this later
            }
        }
    }

}