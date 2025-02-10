package proton.android.authenticator.ui.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import proton.android.authenticator.data.entry.repository.EntryRepository
import proton.android.authenticator.domain.Entry
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    val entries: StateFlow<List<Entry>> = entryRepository.observeAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun submitUri(newUri: String) {
        viewModelScope.launch {
            entryRepository.insertUri(newUri)
        }
    }
}
