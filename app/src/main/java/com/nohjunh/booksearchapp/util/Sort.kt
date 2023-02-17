package com.nohjunh.booksearchapp.util


// Sort 파라미터는 Enum클래스로 사용
// settings에서 사용할 예정.
enum class Sort(val value: String) {
    ACCURACY("accuracy"),
    LATEST("latest")
}