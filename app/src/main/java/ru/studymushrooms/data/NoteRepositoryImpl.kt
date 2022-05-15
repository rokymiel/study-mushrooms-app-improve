package ru.studymushrooms.data

import ru.studymushrooms.api.NoteModel
import ru.studymushrooms.api.ServerApi
import ru.studymushrooms.repository.NoteRepository
import java.util.*

class NoteRepositoryImpl(
    private val serverApi: ServerApi,
) : NoteRepository {
    override suspend fun getNotes(token: String): List<NoteModel> {
        return serverApi.getNotes(token)
    }

    override suspend fun postNote(token: String, title: String, content: String, date: Date) {
        val noteModel =
            NoteModel(title = title, content = content, date = date, author = null, id = null)
        serverApi.postNote(token, noteModel)
    }

    override suspend fun updateNote(token: String, title: String, newContent: String, date: Date) {
        val noteModel = NoteModel(
            title = title,
            content = newContent,
            date = date,
            author = null,
            id = null
        )
        serverApi.updateNote(token, noteModel)
    }

    override suspend fun deleteNote(token: String, title: String, content: String) {
        val noteModel = NoteModel(
            title = title,
            content = content, // нужны ли сюда вообще контент и заголовок, если удаляем?
            date = Date(),
            author = null,
            id = null
        )
        serverApi.deleteNote(token, noteModel)
    }
}