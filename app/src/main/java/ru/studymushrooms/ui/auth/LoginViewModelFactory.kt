package ru.studymushrooms.ui.auth

import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.service_locator.ViewModelFactory

class LoginViewModelFactory: ViewModelFactory<LoginViewModel> {
    override fun create(): LoginViewModel {
        return LoginViewModel(
            ServiceLocator.authorizationRepository,
            ServiceLocator.tokenHolder
        )
    }
}