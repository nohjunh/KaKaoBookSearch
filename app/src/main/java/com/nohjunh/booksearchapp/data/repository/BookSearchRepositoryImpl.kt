package com.nohjunh.booksearchapp.data.repository

import com.nohjunh.booksearchapp.data.api.RetrofitInstance.api
import com.nohjunh.booksearchapp.data.model.SearchResponse
import retrofit2.Response

class BookSearchRepositoryImpl : BookSearchRepository {

    override suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): Response<SearchResponse> {
        // Retrofit API의 searchBooks를 실행해서
        // Response를 반환하도록 구현
        return api.searchBooks(query, sort, page, size)
    }

}