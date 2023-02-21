package com.nohjunh.booksearchapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.work.WorkManager
import com.nohjunh.booksearchapp.data.api.BookSearchApi
import com.nohjunh.booksearchapp.data.db.BookSearchDatabase
import com.nohjunh.booksearchapp.util.Constants.BASE_URL
import com.nohjunh.booksearchapp.util.Constants.DATASTORE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/*
외부 라이브러리 의존객체를 넣기 위한 AppModule 클래스 생성
*/

// AppModule은 앱 전체에서 사용할 모듈이므로
// SingletonComponent에 설치한다.
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*
     Retrofit 의존성
    */
    @Singleton
    @Provides
    // Loging에 사용할 OkHttpClient를 주입하는 provideOkHttpClient 메소드 정의
    fun provideOkHttpClient() : OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    // Retrofit 객체를 주입하기 위한 provideRetrofit 메소드
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder() // 빌더 패턴을 통해 retrofit 객체를 만든다.
            .addConverterFactory(MoshiConverterFactory.create()) // DTO 변환에 사용할 MoshiConverterFactory를 JSON Converter로 설정 만약 Gson을 쓰면 GsonConverterFactory쓰면 됨.
            .client(okHttpClient) //클라이언트 속성에 okHTTP interceptor를 넣어줘서 로그캣에서 패킷내용을 모니터링 (okhttp가 apllication과 서버 사이에서 data를 interceptor할 수 있는 기능이 있기 때문)
            .baseUrl(BASE_URL) // 베이스 URL 전달
            .build() // 객체 생성
    }

    @Singleton
    @Provides
    // BookSearchApi서비스 객체를 주입하기 위한 provideApiService메소드
    fun provideApiService(retrofit : Retrofit) : BookSearchApi {
        // Retrofit의 create메소드로 BookSearchApi의 인스턴스 생성
        return retrofit.create(BookSearchApi::class.java)
    }

    /*
     Room 의존성
    */
    @Singleton
    @Provides
    // BookSearchDatabase 의존 객체를 주입하기 위한 provideBookSearchDatabase 메소드
    // Singleton을 붙였으므로 BookSearchDatabase 객체가 singleton으로 생성됨.
    // @Provides를 통해 외부 라이브러리인 Room Database를 앱 내 필요한 곳에 주입할 수 있도록 함.
    fun provideBookSearchDatabase(@ApplicationContext context: Context) : BookSearchDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            BookSearchDatabase::class.java,
            "favorite-name"
        ).build()

    /*
     DataStore 의존성
    */
    @Singleton
    @Provides
    // PreferenceDataStoreFactory.create를 통해서 singleton 객체를 생성하도록 함.
    // 메인액티비티에서 private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)을 통해
    // 객체를 생성하던 과정과 동일한 과정
    // @ApplicationContext를 통해 context를 얻어와 DataStore 생성 시 필요한 context로 넣어줌.
    fun providePreferenceDataStore(@ApplicationContext context : Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) }
        )

    /*
     WorkManager 의존성
    */
    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context : Context) : WorkManager =
        WorkManager.getInstance(context)

    @Singleton
    @Provides
    // Worker 내부에 주입할 의존성 정의
    // 캐시 최적화 결과를 반영하는 String을 만들어 이거를 Worker클래스에 주입시키면 됨.
    fun provideCacheDeleteResult() : String = "Cache has deleted by Hilt"
}