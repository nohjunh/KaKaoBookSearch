package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.*
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.model.SearchResponse
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import com.nohjunh.booksearchapp.worker.CacheDeleteWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/*
Hilt를 쓰기 전
// BookSearchViewModel은 생성시에 초기값으로 bookSearchRepository를 전달받아야 하는데
// 그냥으로는 ViewModel은 생성 시에 초기값을 받을 수 없기 때문에 factory를 만들어준다.
 */

/* Hilt를 쓰고 난 후 */
@HiltViewModel // @HiltViewModel을 붙여서 BookSearchViewModel을 의존성 주입이 가능한 Scope로 만들어줌.
class BookSearchViewModel @Inject constructor( // @Inject constructor를 이용해 BookSearchRepository와 WorkManger를 주입해줌.
    private val bookSearchRepository: BookSearchRepository,
    private val workManager : WorkManager,
    private val savedStateHandle: SavedStateHandle // 이거는 Module 설정 없이도 자동으로 주입됨.
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
        val response = bookSearchRepository.searchBooks(query, getSortMode(), 1, 15)
        if (response.isSuccessful) {
            response.body()?.let { body ->
                _searchResult.postValue(body)
            }
        }
    }

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

    // favoriteBooks를 그냥 Flow가 아닌
    // StateFlow로 함으로써 flow동작을 favoriteBooks의 lifecycle과
    // 동기화 시킨다.
    /*
    val favoriteBooks: Flow<List<Book>>
        get() = bookSearchRepository.getFavoriteBooks()
    */
    // stateIn을 써서 Flow타입을 StateFlow로 변경해주고
    // scope는 viewModelScope이고, 구독을 시작하는 시점은 WhileSubscribed(5000ms)*
    // why? 5000ms여야 화면을 가로,세로를 바꾸는 과정에서 앱의 lifecycle이 변화한 것인지
    // 아니면 앱이 백그라운드로 가서 변화한 것인지를 파악할 수 있다.
    // listOf 에는 초기값을 넣어준다. Book은 list형식이기에 그냥 ListOf를 넣어줌
    val favoriteBooks: StateFlow<List<Book>>
        get() = bookSearchRepository.getFavoriteBooks()
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(5000), listOf()
            )

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
    // 값을 저장하는 메소드
    fun saveSortMode(value: String) = viewModelScope.launch(Dispatchers.IO) {
        // 파일 작업이므로 IO 디스패처에서 사용
        bookSearchRepository.saveSortMode(value)
    }

    suspend fun getSortMode() = withContext(Dispatchers.IO) {
        // 셋팅값이므로 전체 데이터스트림을 구독할 필요가 없기에 flow에서 first(단일값)를 붙여
        // 단일스트림 값만 가져오고
        // 코루틴에서 반드시 값을 반환하고 종료하게 하는 withContext 내부에서 실행되도록 함.
        // return을 명시하지 않아도 withContext는 값을 반환
        bookSearchRepository.getSortMode().first()
    }

    fun saveCacheDeleteMode(value : Boolean) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.saveCacheDeleteMode(value)
    }

    suspend fun getCacheDeleteMode() = withContext(Dispatchers.IO) {
        bookSearchRepository.getCacheDeleteMode().first()
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

    // WorkManager
    fun setWork() {
        val constraints = Constraints.Builder()
            // 충전 중이거나 배터리 잔량이 낮지 않을 경우에만 workManager가 돌아갈 수 있도록
            // Constraints를 설정
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()

        // 주기적인 workRequest 빌더 설정
        val workRequest = PeriodicWorkRequestBuilder<CacheDeleteWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // 동일한 작업을 중복해서 수행하지 않도록 UniquePeriodicWork 사용
        workManager.enqueueUniquePeriodicWork(
            // 작업의 이름을 WORKER_KEY로 지정
            WORKER_KEY, ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )
    }

    // 작업의 이름이 WORKER_KET인 것을 찾아서 삭제
    fun deleteWork() = workManager.cancelUniqueWork(WORKER_KEY)
    // 현재 queue 내부 에서 WORKER_KEY의 작업 상태를 LiveData타입으로 반환
    fun getWorkStatus() : LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkLiveData(WORKER_KEY)

}