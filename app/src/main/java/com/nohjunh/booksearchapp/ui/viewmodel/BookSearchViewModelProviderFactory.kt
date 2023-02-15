package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository

// bookSearchRepository를 초기값으로 전달받아서
// ViewModel을 반환하는 ViewModel ProviderFactory.

@Suppress("UNCHECKED_CAST")
class BookSearchViewModelProviderFactory(
    private val bookSearchRepository: BookSearchRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BookSearchViewModel::class.java)) {
            return BookSearchViewModel(bookSearchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
}