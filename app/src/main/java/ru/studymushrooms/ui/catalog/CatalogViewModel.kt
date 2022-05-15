package ru.studymushrooms.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.studymushrooms.api.MushroomModel
import ru.studymushrooms.repository.CatalogRepository
import ru.studymushrooms.token.TokenHolder
import ru.studymushrooms.utils.SingleLiveEvent

class CatalogViewModel(
    private val catalogRepository: CatalogRepository,
    private val tokenHolder: TokenHolder,
) : ViewModel() {
    private val _showErrorToastEvents = SingleLiveEvent<String>()
    val showErrorToastEvents: LiveData<String> = _showErrorToastEvents

    private val _mushrooms: MutableLiveData<List<MushroomModel>> = MutableLiveData()
    val mushrooms: LiveData<List<MushroomModel>> = _mushrooms

    fun loadData() {
        if (mushrooms.value == null) {
            viewModelScope.launch {
                try {
                    val mushrooms = withContext(Dispatchers.IO) {
                        catalogRepository.getMushrooms(tokenHolder.getToken(), null, null)
                    }
                    _mushrooms.value = mushrooms
                } catch (t: Throwable) {
                    _showErrorToastEvents.value = t.message
                }
            }
        }
    }
}