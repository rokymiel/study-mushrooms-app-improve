package ru.studymushrooms.ui.catalog

import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.service_locator.ViewModelFactory

class CatalogViewModelFactory: ViewModelFactory<CatalogViewModel> {
    override fun create(): CatalogViewModel {
        return CatalogViewModel(
            ServiceLocator.catalogRepository,
            ServiceLocator.tokenHolder
        )
    }
}