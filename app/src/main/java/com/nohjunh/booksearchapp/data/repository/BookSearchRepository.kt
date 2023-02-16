package com.nohjunh.booksearchapp.data.repository

import androidx.lifecycle.LiveData
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.model.SearchResponse
import retrofit2.Response

// 1. data repository 패키지를 만들고
// 2. repository interface 작성
// 3. repository interface를 구현하는
// 4. Impl class 구현체 작성
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

    fun getFavoriteBooks(): LiveData<List<Book>>

}