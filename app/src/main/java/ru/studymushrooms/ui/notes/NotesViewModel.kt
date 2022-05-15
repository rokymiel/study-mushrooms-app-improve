package ru.studymushrooms.ui.notes

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.NoteModel
import ru.studymushrooms.utils.SingleLiveEvent
import java.util.*

class NotesViewModel : ViewModel() {
    private val _showErrorToastEvents = SingleLiveEvent<String>()
    val showErrorToastEvents: LiveData<String> = _showErrorToastEvents

    private val _notes: MutableLiveData<List<NoteModel>> = MutableLiveData()
    val notes: LiveData<List<NoteModel>> = _notes

    fun loadData() {
        if (notes.value == null) {
            val call = App.api.getNotes(App.token!!)
            call.enqueue(object : Callback<List<NoteModel>> {
                override fun onFailure(call: Call<List<NoteModel>>, t: Throwable) {
                    _showErrorToastEvents.value = t.message
                }

                override fun onResponse(
                    call: Call<List<NoteModel>>,
                    response: Response<List<NoteModel>>
                ) {
                    if (response.isSuccessful) {
                        _notes.value = response.body()
                    } else {
                        _showErrorToastEvents.value = response.errorBody().toString()
                    }
                }
            })
        }
    }

    fun saveNote(title: String, content: String) {
        val noteModel = NoteModel(
            title = title,
            content = content,
            date = Date(),
            author = null,
            id = null
        )

        App.api.postNote(App.token!!, noteModel).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    fun updateNote(title: String, new_content: String) { // todo протестить
        val noteModel = NoteModel(
            title = title,
            content = new_content,
            date = Date(),
            author = null,
            id = null
        )

        App.api.updateNote(App.token!!, noteModel).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    fun deleteNote(title: String, content: String) { // todo протестить
        val noteModel = NoteModel(
            title = title,
            content = content, // нужны ли сюда вообще контент и заголовок, если удаляем?
            date = Date(),
            author = null,
            id = null
        )

        App.api.deleteNote(App.token!!, noteModel).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }
}
