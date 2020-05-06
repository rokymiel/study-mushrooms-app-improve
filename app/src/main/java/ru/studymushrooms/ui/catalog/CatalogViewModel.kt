package ru.studymushrooms.ui.catalog

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.MushroomModel

class CatalogViewModel : ViewModel() {
    val mushrooms: MutableLiveData<List<MushroomModel>> = MutableLiveData()

    fun loadData(context: Context) {
        if (mushrooms.value == null) {
            val call = App.api.getMushrooms(App.token!!, null, null)
            call.enqueue(object : Callback<List<MushroomModel>> {
                override fun onFailure(call: Call<List<MushroomModel>>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Failed to load mushrooms, error ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<List<MushroomModel>>,
                    response: Response<List<MushroomModel>>
                ) {
                    if (response.isSuccessful) {
                        mushrooms.postValue(response.body())
                    } else
                        Toast.makeText(
                            context,
                            "Failed to load mushrooms, error ${response.errorBody()}",
                            Toast.LENGTH_LONG
                        ).show()
                }
            })
        }
    }
}