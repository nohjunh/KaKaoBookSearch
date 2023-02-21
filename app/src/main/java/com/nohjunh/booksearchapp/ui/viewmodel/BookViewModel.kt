package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository,
) : ViewModel() {
    // Room
    // CRUD를 수행하는 suspend함수는 viewModelScope안에서 수행되도록 함.
    // viewModelScope의 기본 디스패처는 Main이기에 파일IO를 수행하는
    // Dispatchers.IO로 컨텍스트 변경
    fun saveBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.insertBooks(book)
    }

}