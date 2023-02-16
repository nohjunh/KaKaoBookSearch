package com.nohjunh.booksearchapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.databinding.ItemBookPreviewBinding

// RecyclerView와 연결할 Adapter
class BookSearchAdapter : ListAdapter<Book, BookSearchViewHolder>(BookDiffCallback) {

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
        val book = currentList[position]
        holder.bind(book)

        holder.itemView.setOnClickListener { view ->
            bookHolderClickListener?.onClick(view, position, book)
            return@setOnClickListener
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