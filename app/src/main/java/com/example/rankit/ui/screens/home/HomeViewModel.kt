package com.example.rankit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rankit.RankItApplication
import com.example.rankit.data.RankItRepository
import com.example.rankit.data.db.entities.RankingListWithCount
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: RankItRepository) : ViewModel() {

    // stateIn converts the Flow from Room into a StateFlow that the UI can read.
    // SharingStarted.WhileSubscribed(5_000) keeps the flow alive for 5 seconds
    // after the last subscriber disappears — survives screen rotation without
    // restarting the DB query.
    val lists: StateFlow<List<RankingListWithCount>> = repository
        .getListsWithCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun deleteList(listId: String) {
        viewModelScope.launch { repository.deleteList(listId) }
    }

    companion object {
        // Factory lets us pass the repository into the ViewModel.
        // Android creates ViewModels for us — we can't call the constructor directly —
        // so we give it a factory that knows how to build one.
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as RankItApplication
                HomeViewModel(app.repository)
            }
        }
    }
}
