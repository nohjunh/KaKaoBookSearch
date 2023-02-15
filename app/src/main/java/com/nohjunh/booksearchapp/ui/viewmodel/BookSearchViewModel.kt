package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nohjunh.booksearchapp.data.model.SearchResponse
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// BookSearchViewModel은 생성시에 초기값으로 bookSearchRepository를 전달받아야 하는데
// 그냥으로는 ViewModel은 생성 시에 초기값을 받을 수 없기 때문에 factory를 만들어준다.
class BookSearchViewModel(
    private val bookSearchRepository: BookSearchRepository
) : ViewModel() {

    // APi
    private val _searchResult = MutableLiveData<SearchResponse>()
    val searchResult: LiveData<SearchResponse>
        get() = _searchResult

    // Repository의 SearchBooks를 코루틴 내부에서 수행하는(viewModelScope)
    // searchBooks 함수를 만들고, 
    // searchBooks에서는 Repository의 searchBooks를 실행하되,
    // 파라미터는 쿼리 이외에는 모두 고정값으로 설정하기로 가정
    // Retrofit 서비스의 리턴값은 MutableLiveData인 _searchResult에 저장
    // 외부에는 수정이 불가능한 searchResult LiveData를 expose 시킬 것이다.
    fun searchBooks(query: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = bookSearchRepository.searchBooks(query, "accuracy", 1, 15)
        if (response.isSuccessful) {
            response.body()?.let { body ->
                _searchResult.postValue(body)
            }
        }
    }

}