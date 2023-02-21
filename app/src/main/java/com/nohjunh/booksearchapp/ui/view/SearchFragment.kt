package com.nohjunh.booksearchapp.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nohjunh.booksearchapp.data.model.Book
import com.nohjunh.booksearchapp.databinding.FragmentSearchBinding
import com.nohjunh.booksearchapp.ui.adapter.BookSearchLoadStateAdapter
import com.nohjunh.booksearchapp.ui.adapter.BookSearchPagingAdapter
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.nohjunh.booksearchapp.util.Constants.SEARCH_BOOKS_TIME_DELAY
import com.nohjunh.booksearchapp.util.collectLatestStateFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewCreatedIns: View

    /* Hilt 사용 전
    // 메인 액티비티에서 초기화 한 ViewModel 가져옴
    private lateinit var bookSearchViewModel: BookSearchViewModel
    */

    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()


    /*
    // ListAdapter로 network 통신한 결과를 RV에 적용하는 경우
    // private lateinit var bookSearchAdapter: BookSearchAdapter
    */

    // PagingAdapter로 network 통신을 한 결과를 가져오는 경우
    private lateinit var bookSearchAdapter: BookSearchPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Hilt 사용 전
        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel
        */

        viewCreatedIns = view

        setupRecyclerView()
        searchBooks()
        setupLoadState()

        /*
        // ListAdapter를 이용해 구독하는 경우
        bookSearchViewModel.searchResult.observe(viewLifecycleOwner, Observer {
            val books = it.documents
            bookSearchAdapter.submitList(books)
        })
        */

        // PagingAdapter를 이용해 구독하는 경우
        // Util의 Extensions.kt 에서 확장함수를 만들어 사용
        collectLatestStateFlow(bookSearchViewModel.searchPagingResult) {
            bookSearchAdapter.submitData(it)
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun searchBooks() {
        var startTime = System.currentTimeMillis()
        var endTime: Long

        binding.etSearch.text =
            Editable.Factory.getInstance().newEditable(bookSearchViewModel.query)

        // editText는 addTextChangedListener를 붙여서
        // text가 입력되면 그 값을 viewModel에 전달해서 ViewModel의 searchBooks()를 실행시킬 것인데,
        // 사람의 입력시간을 고려해서 검색 실행까지 딜레이를 주도록 한다.
        // 처음 입력과 두번 째 입력 사이의 시간이 100L가 넘으면 search동작이 시작되도록 설정
        // 이렇게 해주면 editText에 텍스트를 써줄 때마다 텍스트가 완성되기까지
        // 해당되는 결과값이 바뀌면서 계속 반영될 것이다.
        binding.etSearch.addTextChangedListener { text: Editable? ->
            endTime = System.currentTimeMillis()
            if (endTime - startTime >= SEARCH_BOOKS_TIME_DELAY) {
                text?.let {
                    val query = it.toString().trim()
                    if (query.isNotEmpty()) {
                        /*
                        // 기본적인 Retrofit Network 통신 Flow로 가져오기
                        // bookSearchViewModel.searchBooks(query)
                         */

                        // Paging StateFlow로 가져오기
                        bookSearchViewModel.searchBooksPaging(query)
                        bookSearchViewModel.query = query
                    }
                }
            }
            startTime = endTime
        }
    }

    // LoadState의 Listener를 만들어준다.
    private fun setupLoadState() {
        // 리스너를 단다. 리스너에서는 LoadState값(combinedLoadStates)을 반환 받는다.
        bookSearchAdapter.addLoadStateListener { combinedLoadStates ->
            // combinedLoadState는 PagingSource, RemoteMediator 2가지의 로딩 상태를 가지고 있다.
            // 이 예제에서는 RemoteMediator는 안다루므로 source만 다루면 된다.
            val loadState = combinedLoadStates.source

            // loadState에는
            // 1. Loading 시작시에 만들어지는 prepend, 2.Loading 종료시에 만들어지는 append, 3. Loading값을 갱신할 때 만들어지는 refresh
            // 3가지의 속성을 가지고 있다.
            val isListEmpty = // 리스트가 비어있는지 판정
                bookSearchAdapter.itemCount < 1
                        && loadState.refresh is LoadState.NotLoading
                        && loadState.append.endOfPaginationReached

            // 아래 2개는 각각 반대의 동작을 하면 됨
            binding.tvEmptylist.isVisible = isListEmpty
            binding.rvSearchResult.isVisible = !isListEmpty

            // 로딩중일 때는 progressBar 표시
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            /*
            // 에러 발생 시
            binding.btnRetry.isVisible =
                loadState.refresh is LoadState.Error
                        || loadState.append is LoadState.Error
                        || loadState.prepend is LoadState.Error
            // 에러 발생 시 토스트로 화면에 표시
            val errorState: LoadState.Error? =
                loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                    ?: loadState.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
            }
            */
        }

        /*
        // retry버튼에는 클릭리스너를 달아서 버튼 클릭 시 페이징어댑터를 다시 갱신하도록 함.
        binding.btnRetry.setOnClickListener {
            bookSearchAdapter.retry()
        }
        */
    }

    private fun setupRecyclerView() {
        /*
        // ListAdapter를 적용하는 경우
        bookSearchAdapter = BookSearchAdapter()
        */

        // PagingAdapter를 적용하는 경우
        bookSearchAdapter = BookSearchPagingAdapter()

        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            // LoadStateAdapter를 쓰지 않는 경우
            // binding.rvSearchResult.adapter = bookSearchAdapter

            // LoadStateAdapter를 쓰는 경우
            // 리사이클러뷰에서 어댑터를 셋업을 할 때 withLoadStateFooter를 이용해서 어댑터를 연결하면 되는데
            // footer만 나타나게 한 경우 (header도 나타나게 할 수 있음, 둘 다 나타나게도 가능)
            binding.rvSearchResult.adapter = bookSearchAdapter.withLoadStateFooter(
                footer = BookSearchLoadStateAdapter(bookSearchAdapter::retry)
            )
        }

        /*
        // ListAdatper를 사용하는 경우
        // RV의 clickListener 설정
        bookSearchAdapter.bookHolderClickListener =
            object : BookSearchAdapter.BookHolderClickListener {
                override fun onClick(view: View, positon: Int, book: Book) {
                    val action = SearchFragmentDirections.actionSearchFragmentToBookFragment(book)
                    Navigation.findNavController(viewCreatedIns).navigate(action)
                }

            }
         */

        // PagingAdpater를 사용하는 경우
        // RV의 clickListener 설정
        bookSearchAdapter.bookHolderClickListener =
            object : BookSearchPagingAdapter.BookHolderClickListener {
                override fun onClick(view: View, positon: Int, book: Book) {
                    val action = SearchFragmentDirections.actionSearchFragmentToBookFragment(book)
                    Navigation.findNavController(viewCreatedIns).navigate(action)
                }

            }
    }


}