package com.nohjunh.booksearchapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nohjunh.booksearchapp.data.api.RetrofitInstance.api
import com.nohjunh.booksearchapp.data.db.BookSearchDatabase
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.data.model.SearchResponse
import com.nohjunh.booksearchapp.data.repository.BookSearchRepositoryImpl.PreferencesKeys.SORT_MODE
import com.nohjunh.booksearchapp.util.Constants.PAGING_SIZE
import com.nohjunh.booksearchapp.util.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException

class BookSearchRepositoryImpl(
    // 생성자로 BookSearchDatabase를 받음.
    private val db: BookSearchDatabase,
    // dataStore 객체를 초기값으로 받아줌.
    private val dataStore: DataStore<Preferences>
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

    // DataStore 명세 구현
    private object PreferencesKeys {
        // 저장 및 불러오기에 사용할 Key를 PreferencesKeys에 정의해줌.
        // DataStore은 typeSafe를 위해 PreferencesKey메소드로 사용
        val SORT_MODE = stringPreferencesKey("sort_mode")

    }

    // 저장하는 작업은 코루틴 안에서 일어나야 하기에 함수에 suspend 붙임
    override suspend fun saveSortMode(mode: String) {
        // 전달받은 mode값을 edit 블록 안에서 저장한다.
        dataStore.edit { prefs ->
            prefs[SORT_MODE] = mode
        }
    }

    override suspend fun getSortMode(): Flow<String> {
        // 파일에 접근하기 위해서는 data 메소드를 사용한다.
        return dataStore.data
            // 접근에 실패했을 경우에는 catch로 예외처리 함.
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            // map 블록 안에서 key를 전달해서 flow를 반환 받으면 된다.
            .map { prefs ->
                prefs[SORT_MODE] ?: Sort.ACCURACY.value // 기본 값은 ACCURACY로
            }
    }

    override fun getFavoritePagingBooks(): Flow<PagingData<Book>> {
        val pagingSourceFactory = {
            db.bookSearchDao().getFavoritePagingBooks()
        }
        // 페이저 구현 (config, pagingSourceFactory)
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3 // 페이저가 메모리에 최대로 가질 수 있는 수
            ),
            // getFavoritePagingBooks() 결과를 Factory에 전달
            pagingSourceFactory = pagingSourceFactory
        ).flow // flow를 붙여서 페이저를 flow로 만들어줌.

    }


}