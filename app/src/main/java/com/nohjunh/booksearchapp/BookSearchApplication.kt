package com.nohjunh.booksearchapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
// BookSearchApplication이 Configuration.Provider를 구현하게 하고
// getWorkManagerConfiguration()를 정의하면
// Worker클래스가 HiltWorkerFactory를 통해 생성된다.
class BookSearchApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory : HiltWorkerFactory
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }


}