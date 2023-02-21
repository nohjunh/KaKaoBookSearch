package com.nohjunh.booksearchapp.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/*
HiltWorker, @AssistedInject constructor를 써서
Worker클래스가 Hilt 의존성을 주입 받을 수 있게 만들어 줌

매개변수들은 @Assisted를 통해 주입받을 수 있도록 만들어 줌.
이렇게 정의한 Worker클래스는
HiltWorkerFactory를 통해 직접 생성해줘야 된다.
BookSearchAplication에서 생성해서 설정해주면 된다.
 */
@HiltWorker
class CacheDeleteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cacheDeleteResult : String
) : Worker(context, workerParams) {
    // doWork에서 백그라운드 작업을 정의
    override fun doWork(): Result {
        // 캐시 제거 상황 가정
        return try {
            //Log.d("WorkManager", "Cache has successfully deleted")
            Log.d("WorkManager", cacheDeleteResult)
            // 작업성공
            Result.success()
        } catch (exception : Exception) {
            exception.printStackTrace()
            // 작업 실패
            Result.failure()
        }
    }

}