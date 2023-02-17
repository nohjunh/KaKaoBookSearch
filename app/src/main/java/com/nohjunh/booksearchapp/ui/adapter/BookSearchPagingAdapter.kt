package com.nohjunh.booksearchapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.databinding.ItemBookPreviewBinding

// Paging이므로 ListAdapter와는 달리 PagingDataAdapter를 상속 받음
class BookSearchPagingAdapter : PagingDataAdapter<Book, BookSearchViewHolder>(BookDiffCallback) {
    // BookSearchAdapter에서 각 itemHolder에 대한 클릭이벤트 리스너 설정
    interface BookHolderClickListener {
        fun onClick(view: View, positon: Int, book: Book)
    }

    var bookHolderClickListener: BookHolderClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchViewHolder {
        return BookSearchViewHolder(
            ItemBookPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // 구체적인 데이터를 바인딩
    override fun onBindViewHolder(holder: BookSearchViewHolder, position: Int) {
        // Flow나 LiveData는 currentList[position]으로 book인스턴스를 가져오지만(ListAdapter일 때)
        // paging은 getItem(position) 으로 가져옴
        val pagedBook = getItem(position)
        // getItem은 null이 될 수 있기에 null 처리를 해줌.
        pagedBook?.let { book ->
            holder.bind(book)
            holder.itemView.setOnClickListener { view ->
                bookHolderClickListener?.onClick(view, position, book)
                return@setOnClickListener
            }
        }

    }

    // DiffUtil 작동을 위한 Callback
    companion object {
        private val BookDiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.isbn == newItem.isbn
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }
}