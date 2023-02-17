package com.nohjunh.booksearchapp.data.db

import androidx.room.*
import com.nohjunh.booksearchapp.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSearchDAO {

    // 이러한 로컬 DB 작업은 시간이 걸리는 작업이기에
    // 비동기로 돌리기위해 코루틴을 사용하고 suspend로 함수를 지정

    // DB에 동일한 데이터가 들어갈 경우 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books")
    fun getFavoriteBooks(): Flow<List<Book>>


}