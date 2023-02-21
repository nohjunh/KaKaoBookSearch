package com.nohjunh.booksearchapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import com.nohjunh.booksearchapp.worker.CacheDeleteWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository,
    private val workManager : WorkManager,
) : ViewModel() {

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


    companion object {
        private val WORKER_KEY =  "cache_worker"
    }


}