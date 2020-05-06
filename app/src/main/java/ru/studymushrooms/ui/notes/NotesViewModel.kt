package ru.studymushrooms.ui.notes

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.NoteModel
import java.util.*

class NotesViewModel : ViewModel() {

    val notes: MutableLiveData<List<NoteModel>> = MutableLiveData()
    private val note: MutableLiveData<NoteModel> = MutableLiveData()

    fun loadData(context: Context) {
        if (notes.value == null) {
            val call = App.api.getNotes(App.token!!)
            call.enqueue(object : Callback<List<NoteModel>> {
                override fun onFailure(call: Call<List<NoteModel>>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Failed to load notes, error ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<List<NoteModel>>,
                    response: Response<List<NoteModel>>
                ) {
                    if (response.isSuccessful) {
                        notes.postValue(response.body())
                    } else
                        Toast.makeText(
                            context,
                            "Failed to load notes, error ${response.errorBody()}",
                            Toast.LENGTH_LONG
                        ).show()
                }
            })
        }
    }

    fun saveNote(title: String, content: String) {
        val n = NoteModel(
            title = title,
            content = content,
            date = Date(),
            author = null,
            id = null
        )

        App.api.postNote(App.token!!, n).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })

        note.postValue(
            n
        )
    }
}
