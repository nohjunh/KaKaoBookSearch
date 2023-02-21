package com.nohjunh.booksearchapp.di

import com.nohjunh.booksearchapp.data.repository.BookSearchRepository
import com.nohjunh.booksearchapp.data.repository.BookSearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/*
    interface인 Repository를 주입하기 위한
    Repository Module 작성
*/

// BookSearchRepository는 interface이기 때문에
// @Binds를 사용해서 Hilt가 BookSearchRepository 의존성 객체를
// 생성할 수 있도록 정보를 제공해줌
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule  {

    @Singleton
    @Binds
    abstract fun bindBookSearchRepository(
        bookSearchRepositoryImpl: BookSearchRepositoryImpl
    ) : BookSearchRepository

}