package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository,
    private val savedStateHandle: SavedStateHandle // 이거는 Module 설정 없이도 자동으로 주입됨.

) : ViewModel() {
    // Paging
    // Retrofit
    private val _searchPagingResult = MutableStateFlow<PagingData<Book>>(PagingData.empty())
    val searchPagingResult: StateFlow<PagingData<Book>>
        get() = _searchPagingResult.asStateFlow()

    // searchBooksPaging 응답을 StateFlow로 표시하기 위해서
    // _searchPagingResult를 MutableStateFlow 타입으로 선언
    // searchBooksPaging의 결과가 _searchPagingResult를 갱신하도록 하되,
    // UI에는 변경 불가능한 searchPagingResult StateFlow를 노출시킴.
    fun searchBooksPaging(query: String) {
        viewModelScope.launch {
            bookSearchRepository.searchBooksPaging(query, getSortMode())
                .cachedIn(viewModelScope)
                .collect {
                    _searchPagingResult.value = it
                }
        }
    }

    // --SavedState--
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
        private val WORKER_KEY =  "cache_worker"
    }


    // DataStore
    suspend fun getSortMode() = withContext(Dispatchers.IO) {
        // 셋팅값이므로 전체 데이터스트림을 구독할 필요가 없기에 flow에서 first(단일값)를 붙여
        // 단일스트림 값만 가져오고
        // 코루틴에서 반드시 값을 반환하고 종료하게 하는 withContext 내부에서 실행되도록 함.
        // return을 명시하지 않아도 withContext는 값을 반환
        bookSearchRepository.getSortMode().first()
    }

}