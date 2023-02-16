package com.nohjunh.booksearchapp.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


// Room은 ORM에 Primitive와 BoxToBox만 사용하도록 제한하고 있음.
// Room에서 Primitive가 아닌 일반 객체 or List<String> 등등으로 ORM을 수정하게 되면
// 처리 속도가 느려지며 메모리 낭비가 심함.
// TypeConverter를 사용해서 data를 Primitive Type으로 변화시키면 됨.
// Room은 List<Stirng> 같이 Primitive Type이 아닌 게 있으면
// DB에 저장하기 위해 String으로 변환해준다.
// 이러한 데이터 직렬화에 kotlinx.serialization library을 사용한다.

class OrmConverter {

    // List<String>이 들어오면 encodeToString으로 String으로 encoding 시켜주는 컨버터
    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    // String을 받으면 List<String>으로 decode해주는 컨버터
    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}