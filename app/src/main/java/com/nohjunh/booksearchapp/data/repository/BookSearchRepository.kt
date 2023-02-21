package com.nohjunh.booksearchapp.data.repository

import androidx.paging.PagingData
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

// 1. data repository 패키지를 만들고
// 2. repository interface 작성
// 3. repository interface를 구현하는
// 4. Impl class 구현체 작성

// interface는 함수 명세를 적기 위함.
interface BookSearchRepository {

    // book search API를 사용하기 위한 메소드 interface 정의
    suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): Response<SearchResponse>

    // Room Dao를 조작하기 위한 메소드 inteface 정의
    suspend fun insertBooks(book: Book)

    suspend fun deleteBooks(book: Book)

    fun getFavoriteBooks(): Flow<List<Book>>

    // DataStore
    suspend fun saveSortMode(mode: String)

    suspend fun getSortMode(): Flow<String>

    // WorkManager
    suspend fun saveCacheDeleteMode(mode : Boolean)
    suspend fun getCacheDeleteMode() : Flow<Boolean>

    // Paging(Room DB)
    fun getFavoritePagingBooks(): Flow<PagingData<Book>>

    // Paging(Retrofit)
    fun searchBooksPaging(query: String, sort: String): Flow<PagingData<Book>>
}