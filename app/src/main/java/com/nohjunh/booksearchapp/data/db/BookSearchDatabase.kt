package com.nohjunh.booksearchapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nohjunh.booksearchapp.data.model.Book

// ROOM에서 사용할 엔티티와 버전 지정
@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OrmConverter::class) // Room이 알아서 TypeConverter가 필요한 순간에 사용함.
abstract class BookSearchDatabase : RoomDatabase() {

    // ROOM에서 사용할 DAO 지정
    abstract fun bookSearchDao(): BookSearchDAO

    /*
    Hile Module화 했으므로 더이상 필요 없음.

    // 중복으로 생성하지 않도록 singleton 설정
    companion object {
        @Volatile
        private var INSTANCE: BookSearchDatabase? = null

        private fun buildDatabase(context: Context): BookSearchDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                BookSearchDatabase::class.java,
                "favorite-name"
            ).build()

        fun getInstance(context: Context): BookSearchDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }
    }
     */

}