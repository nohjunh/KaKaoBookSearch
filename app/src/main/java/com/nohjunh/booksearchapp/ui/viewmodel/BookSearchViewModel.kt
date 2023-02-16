package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.*
import com.nohjunh.booksearchapp.data.model.SearchResponse
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// BookSearchViewModel은 생성시에 초기값으로 bookSearchRepository를 전달받아야 하는데
// 그냥으로는 ViewModel은 생성 시에 초기값을 받을 수 없기 때문에 factory를 만들어준다.
class BookSearchViewModel(
    private val bookSearchRepository: BookSearchRepository,
    private val savedStateHandle: SavedStateHandle
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

    // SavedState
    
    // 저장 및 로드에 사용할 SAVE_STATE_KEY정의 후
    // edittext에 입력한 query를 보존할 var query 정의
    // query의 값이 벼경되면 그 값을 savedStateHandle에 set저장하도록 설정
    var query = String()
        set(value) {
            field = value
            savedStateHandle.set(SAVE_STATE_KEY, value)
        }

    // viewModel을 초기화할 때 query 초기값을 설정하도록 함.
    init {
        query = savedStateHandle.get<String>(SAVE_STATE_KEY) ?: ""
    }

    companion object {
        private const val SAVE_STATE_KEY = "query"
    }

}