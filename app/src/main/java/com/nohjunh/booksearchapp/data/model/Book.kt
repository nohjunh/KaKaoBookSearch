package com.nohjunh.booksearchapp.data.model


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
// Book을 LiveData로 설정해 변화를 감지할 것이다.
// data class Book()을 Entitiy로 해줬고
// Entity테이블의 변화가 있을 때마다 변화를 감지하게 될 것이다.
@Entity(tableName = "books")
data class Book(
    // ROOM에서는 List<>와 같이 Primitive Type이 아닌 데이터형이 있을 수도 있다.
    // ROOM은 Primitive Type을 기본적으로 받기 때문에
    // Type Converter를 통해 data를 Primitive Type으로 바꾸면 된다.
    val authors: List<String>,
    val contents: String,
    val datetime: String,
    // 고유키를 isbn으로 지정
    @PrimaryKey(autoGenerate = false)
    val isbn: String,
    val price: Int,
    val publisher: String,
    // 기본적으로 RoomDB는 대소문자 구분을 하지 않음
    // 또한, 따로 설정이 없으면 DB의 key값이 변수명과 동일하게
    // 설정되는데
    // @ColumnInfo을 통해 sale_price 변수명으로 들어오는
    // 데이터를 salePrice라는 변수명으로 매핑시킴.
    @ColumnInfo(name = "sale_price")
    @field:Json(name = "sale_price")
    val salePrice: Int,
    val status: String,
    val thumbnail: String,
    val title: String,
    val translators: List<String>,
    val url: String // 해당 책의 상세설명 페이지 url을 나타냄
) : Parcelable