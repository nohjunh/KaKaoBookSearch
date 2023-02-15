package com.nohjunh.booksearchapp.data.api

import com.nohjunh.booksearchapp.data.model.SearchResponse
import com.nohjunh.booksearchapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Retrofit은 HTTP API의 request를 interface로 정의해서 사용한다.
// data패키지 안에 APi 패키지 안에 Retrofit에 필요한
// 서비스를 만든다.
// 서비스는 API키와 인자를 전달받아 Book API에다가 GET요청을 하는 서비스
interface BookSearchApi {

    @Headers("Authorization: KaKaoAK $API_KEY") // 인증에 필요한 헤더
    @GET("v3/search/book") // API
    suspend fun searchBooks(
        // 파라미터는 Query 어노테이션을 써서 전달
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<SearchResponse> // SearchResponse클래스를 가지는 Response를 반환
}