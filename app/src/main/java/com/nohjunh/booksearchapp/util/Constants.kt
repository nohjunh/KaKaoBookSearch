package com.nohjunh.booksearchapp.util

import com.nohjunh.booksearchapp.BuildConfig

// Request 요청에 사용할 URL과 API키 정보를
// util 패키지의 Constants 클래스 안에 모아놓는다.
object Constants {
    const val BASE_URL = "https://dapi.kakao.com/"
    const val API_KEY = BuildConfig.bookApiKey
}