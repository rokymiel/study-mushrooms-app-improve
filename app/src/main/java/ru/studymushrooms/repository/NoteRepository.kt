package ru.studymushrooms.repository

import ru.studymushrooms.api.NoteModel
import java.util.*

interface NoteRepository {
    suspend fun getNotes(token: String): List<NoteModel>

    suspend fun postNote(token: String, title: String, content: String, date: Date)

    suspend fun updateNote(token: String, title: String, newContent: String, date: Date)

    suspend fun deleteNote(token: String, title: String, content: String)
}