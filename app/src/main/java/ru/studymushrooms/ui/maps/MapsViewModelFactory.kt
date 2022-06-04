package ru.studymushrooms.ui.maps

import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.service_locator.ViewModelFactory

class MapsViewModelFactory: ViewModelFactory<MapsViewModel> {
    override fun create(): MapsViewModel {
        return MapsViewModel(
            ServiceLocator.placesRepository,
            ServiceLocator.tokenHolder
        )
    }
}