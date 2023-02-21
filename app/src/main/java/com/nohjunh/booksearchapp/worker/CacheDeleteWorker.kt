package com.nohjunh.booksearchapp.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class CacheDeleteWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams){
    // doWork에서 백그라운드 작업을 정의
    override fun doWork(): Result {
        // 캐시 제거 상황 가정
        return try {
            Log.d("WorkManager", "Cache has successfully deleted")
            // 작업성공
            Result.success()
        } catch (exception : Exception) {
            exception.printStackTrace()
            // 작업 실패
            Result.failure()
        }
    }

}