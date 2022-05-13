package ru.studymushrooms.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.PlaceModel

class MapsViewModel : ViewModel() {
    private val _places = MutableLiveData<List<PlaceModel>>()
    val places: LiveData<List<PlaceModel>> = _places

    fun getPlaces() {
        val call = App.api.getPlaces(App.token!!)

        call.enqueue(object : Callback<List<PlaceModel>> {
            override fun onResponse(
                call: Call<List<PlaceModel>>,
                response: Response<List<PlaceModel>>
            ) {
                if (response.isSuccessful) {
                    _places.value = response.body()!!
                }
            }

            override fun onFailure(call: Call<List<PlaceModel>>, t: Throwable) {
                throw t
            }
        })
    }

}