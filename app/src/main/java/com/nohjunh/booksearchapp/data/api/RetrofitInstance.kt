package com.nohjunh.booksearchapp.data.api

import com.nohjunh.booksearchapp.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// API서비스를 사용하기 위한 Retrofit instance
// 여러 개의 Retrofit 객체가 만들어지면
// 리소스도 낭비되고 통신에 혼선 및 복잡성이 커질 수 있다.
// 그래서 object class와 by lazy를 사용해
// 실제 사용될 때 만들어지고 단 하나의 instance만 만들어질 수 있도록
// singleton으로 구현함.
object RetrofitInstance {
    private val okHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder() // 빌더 패턴을 통해 retrofit 객체를 만든다.
            .addConverterFactory(MoshiConverterFactory.create()) // DTO 변환에 사용할 MoshiConverterFactory를 JSON Converter로 설정 만약 Gson을 쓰면 GsonConverterFactory쓰면 됨.
            .client(okHttpClient) //클라이언트 속성에 okHTTP interceptor를 넣어줘서 로그캣에서 패킷내용을 모니터링 (okhttp가 apllication과 서버 사이에서 data를 interceptor할 수 있는 기능이 있기 때문)
            .baseUrl(BASE_URL) // 베이스 URL 전달
            .build() // 객체 생성
    }

    val api: BookSearchApi by lazy {
        // Retrofit의 create메소드로 BookSearchApi의 인스턴스 생성
        retrofit.create(BookSearchApi::class.java)
    }
}