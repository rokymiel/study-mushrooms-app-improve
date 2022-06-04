package ru.studymushrooms.service_locator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelCreator(
    private val viewModelFactoriesMap: Map<Class<out ViewModel>, ViewModelFactory<out ViewModel>>
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelFactoriesMap[modelClass]!!.create() as T
    }
}