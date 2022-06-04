package ru.studymushrooms.ui.recognize

import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.service_locator.ViewModelFactory

class RecognitionViewModelFactory: ViewModelFactory<RecognitionViewModel> {
    override fun create(): RecognitionViewModel {
        return RecognitionViewModel(
            ServiceLocator.recognitionRepository,
            ServiceLocator.tokenHolder
        )
    }
}