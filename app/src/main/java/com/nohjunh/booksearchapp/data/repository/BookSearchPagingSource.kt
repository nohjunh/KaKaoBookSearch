package com.nohjunh.booksearchapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nohjunh.booksearchapp.data.api.RetrofitInstance.api
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.util.Constants.PAGING_SIZE
import retrofit2.HttpException
import java.io.IOException


// 페이징 소스<페이지 타입, 데이터타입>를 상속 받음.
// 페이지 타입은 1,2,..3 이런 식이므로 Int로 지정
class BookSearchPagingSource(
    private val query: String,
    private val sort: String
) : PagingSource<Int, Book>() {

    // getRefreshKey()는 페이지를 갱신해야될 때 사용하는 함수
    // 가장 최근에 접근 한 페이지를 state.acnchorPosition으로 받고
    // 그 주위의 페이지를 읽어오도록 키를 반환해주는 역할을 함 .
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // load()는 Pager가 데이터를 호출할 때마다 불리는 함수
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        return try {
            // params.key로부터 페이지넘버를 받아옴
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            // 그 값을 Retrofit Service에 전달해서 해당하는 데이터를 받아옴
            val response = api.searchBooks(query, sort, pageNumber, params.loadSize)
            // api가 반환해주는 값에서 meta.isEnd를 통해 페이지가 끝인지를 알 수 있음.
            val endOfPaginationReached = response.body()?.meta?.isEnd!!

            val data = response.body()?.documents!!
            // 페이지가 1이면 prevKey는 null이 되도록 함.
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (endOfPaginationReached) {
                null
            } else {
                pageNumber + (params.loadSize / PAGING_SIZE)
            }
            // 결과Data와 이전 페이지, 다음 페이지의 키 값을 LoadResult.Page에 담아서 반환해주면 됨.
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        // 초기 값은 null 이므로 companion object에서 1로 지정
        const val STARTING_PAGE_INDEX = 1
    }


}