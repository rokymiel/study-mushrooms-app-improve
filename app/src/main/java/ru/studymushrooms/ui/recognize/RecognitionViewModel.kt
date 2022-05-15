package ru.studymushrooms.ui.recognize

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocoahero.android.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.studymushrooms.R
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.repository.RecognitionRepository
import ru.studymushrooms.token.TokenHolder
import ru.studymushrooms.utils.SingleLiveEvent
import java.io.ByteArrayOutputStream

enum class RecognitionRequestResult(@StringRes val res: Int) {
    SUCCESS(R.string.place_success),
    ERROR(R.string.place_error);
}

sealed class RecognitionEvents {
    data class ShowRecognitionResult(val result: RecognitionRequestResult) : RecognitionEvents()

    data class ShowLocationDialog(
        val b64Image: String,
        val location: Point?,
        val showOptions: Boolean = false
    ) : RecognitionEvents()
}

class RecognitionViewModel(
    private val recognitionRepository: RecognitionRepository,
    private val tokenHolder: TokenHolder,
) : ViewModel() {
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

        viewModelScope.launch {
            try {
                val models = withContext(Dispatchers.IO) {
                    recognitionRepository.recognize(tokenHolder.getToken(), b64Image)
                }
                _recognitions.value = models.map { RecognitionItem(it) }
                _mushroomImage.value = bitmap
                _recognitionEvents.value =
                    RecognitionEvents.ShowLocationDialog(b64Image, imageLocation, isFromStorage)
            } catch (t: Throwable) {
                Log.e("Recognition", t.message.orEmpty())
            }
        }
    }

    fun savePlace(location: Point?, b64Image: String) {
        viewModelScope.launch {
            try {
                recognitionRepository.addPlace(
                    tokenHolder.getToken(),
                    PlaceModel(
                        rawImage = b64Image,
                        location = location,
                        date = null,
                        useLocation = location != null,
                        longitude = null,
                        latitude = null
                    )
                )
                _recognitionEvents.value =
                    RecognitionEvents.ShowRecognitionResult(RecognitionRequestResult.SUCCESS)
            } catch (t: Throwable) {
                _recognitionEvents.value =
                    RecognitionEvents.ShowRecognitionResult(RecognitionRequestResult.ERROR)
            }
        }
    }
}