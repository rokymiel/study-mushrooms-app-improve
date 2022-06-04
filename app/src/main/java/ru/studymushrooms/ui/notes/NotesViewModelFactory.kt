package ru.studymushrooms.ui.notes

import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.service_locator.ViewModelFactory

class NotesViewModelFactory: ViewModelFactory<NotesViewModel> {
    override fun create(): NotesViewModel {
        return NotesViewModel(ServiceLocator.noteRepository, ServiceLocator.tokenHolder)
    }
}