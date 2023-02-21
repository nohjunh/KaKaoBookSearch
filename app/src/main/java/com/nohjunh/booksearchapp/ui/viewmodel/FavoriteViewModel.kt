package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository,

) : ViewModel() {
    // Room
    // CRUD를 수행하는 suspend함수는 viewModelScope안에서 수행되도록 함.
    // viewModelScope의 기본 디스패처는 Main이기에 파일IO를 수행하는
    // Dispatchers.IO로 컨텍스트 변경
    fun saveBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.insertBooks(book)
    }

    fun deleteBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.deleteBooks(book)
    }

    // Paging
    // Room DB
    val favoritePagingBooks: StateFlow<PagingData<Book>> =
    // getFavoritePagingBooks() 응답에다가 cachedIn을 붙여서 코루틴이 데이터스트림을 캐시하고
    // 공유 가능하게 만든다.
        // UI에서 변화를 감시해야 하는 데이터이기 때문에 stateIn을 써서 StateFlow로 만들어줌
        bookSearchRepository.getFavoritePagingBooks()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())



}