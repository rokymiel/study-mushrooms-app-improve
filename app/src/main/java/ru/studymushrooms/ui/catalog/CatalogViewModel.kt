package ru.studymushrooms.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.MushroomModel
import ru.studymushrooms.utils.SingleLiveEvent

class CatalogViewModel : ViewModel() {
    private val _showErrorToastEvents = SingleLiveEvent<String>()
    val showErrorToastEvents: LiveData<String> = _showErrorToastEvents

    private val _mushrooms: MutableLiveData<List<MushroomModel>> = MutableLiveData()
    val mushrooms: LiveData<List<MushroomModel>> = _mushrooms

    fun loadData() {
        if (mushrooms.value == null) {
            val call = App.api.getMushrooms(App.token!!, null, null)
            call.enqueue(object : Callback<List<MushroomModel>> {
                override fun onFailure(call: Call<List<MushroomModel>>, t: Throwable) {
                    _showErrorToastEvents.value = t.message
                }

                override fun onResponse(
                    call: Call<List<MushroomModel>>,
                    response: Response<List<MushroomModel>>
                ) {
                    if (response.isSuccessful) {
                        _mushrooms.value = response.body()
                    } else {
                        _showErrorToastEvents.value = response.errorBody().toString()
                    }
                }
            })
        }
    }
}