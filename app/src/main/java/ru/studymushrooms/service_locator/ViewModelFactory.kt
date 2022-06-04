package ru.studymushrooms.service_locator

import androidx.lifecycle.ViewModel

interface ViewModelFactory<T: ViewModel> {
    fun create(): T
}