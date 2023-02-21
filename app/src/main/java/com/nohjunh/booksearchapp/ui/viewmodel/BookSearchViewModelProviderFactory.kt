package com.nohjunh.booksearchapp.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import androidx.work.WorkManager
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository

// bookSearchRepository를 초기값으로 전달받아서
// ViewModel을 반환하는 ViewModel ProviderFactory.

//@Suppress("UNCHECKED_CAST")
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


/*
// ViewModel이 delegate(by viewModels, by activityViewModels)에 의해
// 만들어지므로 더 이상 ViewModelFactory가 필요 없음
// stateStateHandle을 사용할 것이므로 ViewModelProviderFactory도 변경
class BookSearchViewModelProviderFactory(
    private val bookSearchRepository: BookSearchRepository,
    private val workManager : WorkManager,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(BookSearchViewModel::class.java)) {
            return BookSearchViewModel(bookSearchRepository, workManager, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/