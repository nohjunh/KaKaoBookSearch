package com.nohjunh.booksearchapp.data.repository

import com.nohjunh.booksearchapp.data.api.RetrofitInstance.api
import com.nohjunh.booksearchapp.data.db.BookSearchDatabase
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class BookSearchRepositoryImpl(
    // 생성자로 BookSearchDatabase를 받음.
    private val db: BookSearchDatabase
) : BookSearchRepository {

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

    override suspend fun insertBooks(book: Book) {
        db.bookSearchDao().insertBook(book)
    }

    override suspend fun deleteBooks(book: Book) {
        db.bookSearchDao().deleteBook(book)
    }

    override fun getFavoriteBooks(): Flow<List<Book>> {
        return db.bookSearchDao().getFavoriteBooks()
    }


}