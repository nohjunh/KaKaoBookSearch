package com.nohjunh.booksearchapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.databinding.FragmentFavoriteBinding
import com.nohjunh.booksearchapp.ui.adapter.BookSearchAdapter
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModel


class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewCreatedIns: View

    private lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var bookSearchAdapter: BookSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        viewCreatedIns = view

        setupRecyclerView()
        setupTouchHelper(view)

        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner, Observer {
            bookSearchAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() {
        bookSearchAdapter = BookSearchAdapter()
        binding.rvFavoriteBooks.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            binding.rvFavoriteBooks.adapter = bookSearchAdapter
        }

        // RV의 clickListener 설정
        bookSearchAdapter.bookHolderClickListener =
            object : BookSearchAdapter.BookHolderClickListener {
                override fun onClick(view: View, positon: Int, book: Book) {
                    val action = SearchFragmentDirections.actionSearchFragmentToBookFragment(book)
                    Navigation.findNavController(viewCreatedIns).navigate(action)
                }
            }
    }

    // item이 왼쪽으로 스와이프되면 데이터가 삭제되도록 셋팅
    private fun setupTouchHelper(view: View) {
        // RecyclerView의 item을 스와이프 하는데에는 SimpleCallback을 사용함.
        // 우선 SimpleCallback의 인스턴스를 만들고
        // attachToRecyclerView로 binding.rvFavoriteBooks (=리사이클러뷰)를 연결시켜주면
        // 각 아이템이 스와이프나 드래그 동작을 인식할 수 있게 된다.
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            // 인식할 드래그 방향은 dragDirs로 설정하는데 여기서는 드래그는
            // 사용하지 않으므로 0으로 설정
            // 스와이프 방향은 LEFT만 인식시킴
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // onMove는 사용하지 않을 것이므로 return true로 설정
                return true
            }

            // onSwiped에서는 스와이프 동작이 발생했을 떄 동작을 정의한다.
            // 여기서 데이터를 삭제할 것이다.
            // 터치한 ViewHolder 위치를 getbindingAdapterPosition으로
            // 획득한 다음에 Adapter의 currentList에 넣어
            // 해당하는 item의 book 인스턴스를 획득한다.
            // 그 후 delete를 진행한다.
            // 만약, 데이터를 지운 상태를 되돌리고 싶은 경우를 위해
            // SnackBar의 setAction의 "Undo"를 이용해 ("Undo"는 사용자 지정이기에 마음대로 바꿔도 됨.)
            // 다시 되돌릴 수 있도록 saveBook(book)을 실행해 다시 시작한다.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val book = bookSearchAdapter.currentList[position]
                bookSearchViewModel.deleteBook(book)
                Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        bookSearchViewModel.saveBook(book)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}