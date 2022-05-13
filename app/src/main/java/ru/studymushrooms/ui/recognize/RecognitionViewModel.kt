package ru.studymushrooms.ui.recognize

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cocoahero.android.geojson.Point
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R
import ru.studymushrooms.api.ImageRequest
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.api.RecognitionModel
import ru.studymushrooms.utils.SingleLiveEvent
import java.io.ByteArrayOutputStream

enum class RecognitionRequestResult(@StringRes val res: Int) {
    SUCCESS(R.string.login_error),
    ERROR(R.string.login_error);
}

sealed class RecognitionEvents {
    data class ShowRecognitionResult(val result: RecognitionRequestResult) : RecognitionEvents()

    data class ShowLocationDialog(
        val b64Image: String,
        val location: Point?,
        val showOptions: Boolean = false
    ) : RecognitionEvents()
}

class RecognitionViewModel : ViewModel() {
    private val _recognitionEvents = SingleLiveEvent<RecognitionEvents>()
    val recognitionEvents: LiveData<RecognitionEvents> = _recognitionEvents

    private val _recognitions = MutableLiveData<List<RecognitionItem>>()
    val recognitions: LiveData<List<RecognitionItem>> = _recognitions

    private val _mushroomImage = MutableLiveData<Bitmap>()
    val mushroomImage: LiveData<Bitmap> = _mushroomImage

    fun recognize(bitmap: Bitmap, isFromStorage: Boolean, imageLocation: Point?) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val b64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

        App.api.recognize(
            App.token!!, ImageRequest(b64Image)
        ).enqueue(object :
            Callback<List<RecognitionModel>> {
            override fun onFailure(call: Call<List<RecognitionModel>>, t: Throwable) {
                Log.e("Recognition", t.message.orEmpty())
            }

            override fun onResponse(
                call: Call<List<RecognitionModel>>,
                response: Response<List<RecognitionModel>>
            ) {
                if (response.isSuccessful) {
                    _recognitions.value = response.body()!!.map { RecognitionItem(it) }
                    _mushroomImage.value = bitmap
                    _recognitionEvents.value =
                        RecognitionEvents.ShowLocationDialog(b64Image, imageLocation, isFromStorage)
                } else {
                    Log.d("Recognition", response.errorBody().toString())
                }
            }
        })
    }

    fun savePlace(location: Point?, b64Image: String) {
        val call = App.api.addPlace(
            App.token!!, PlaceModel(
                rawImage = b64Image,
                location = location,
                date = null,
                useLocation = location != null,
                longitude = null,
                latitude = null
            )
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable
            ) {
                _recognitionEvents.value =
                    RecognitionEvents.ShowRecognitionResult(RecognitionRequestResult.ERROR)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful)
                    _recognitionEvents.value =
                        RecognitionEvents.ShowRecognitionResult(RecognitionRequestResult.SUCCESS)
                else {
                    _recognitionEvents.value =
                        RecognitionEvents.ShowRecognitionResult(RecognitionRequestResult.ERROR)
                }
            }
        })
    }
}