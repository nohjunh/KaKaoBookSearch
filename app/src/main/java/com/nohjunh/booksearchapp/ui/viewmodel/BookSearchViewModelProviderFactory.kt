package com.nohjunh.booksearchapp.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository

// bookSearchRepository를 초기값으로 전달받아서
// ViewModel을 반환하는 ViewModel ProviderFactory.

@Suppress("UNCHECKED_CAST")
//class BookSearchViewModelProviderFactory(
//    private val bookSearchRepository: BookSearchRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//
//        if (modelClass.isAssignableFrom(BookSearchViewModel::class.java)) {
//            return BookSearchViewModel(bookSearchRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

// stateStateHandle을 사용할 것이므로 ViewModelProviderFactory도 변경
class BookSearchViewModelProviderFactory(
    private val bookSearchRepository: BookSearchRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(BookSearchViewModel::class.java)) {
            return BookSearchViewModel(bookSearchRepository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}