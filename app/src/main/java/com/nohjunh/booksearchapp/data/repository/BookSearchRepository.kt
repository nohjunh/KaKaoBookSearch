package com.nohjunh.booksearchapp.data.repository

import com.nohjunh.booksearchapp.data.model.SearchResponse
import retrofit2.Response

// 1. data repository 패키지를 만들고
// 2. repository interface 작성
// 3. repository interface를 구현하는
// 4. Impl class 구현체 작성
interface BookSearchRepository {

    suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): Response<SearchResponse>

}