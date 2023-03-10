package com.nohjunh.booksearchapp.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.nohjunh.booksearchapp.databinding.FragmentBookBinding
import com.nohjunh.booksearchapp.ui.viewmodel.BookViewModel
import dagger.hilt.android.AndroidEntryPoint

// Fragment에 @AndroidEntryPoint를 붙여서 의존성 주입이 가능한 Scope로 만들어준다.
@AndroidEntryPoint
class BookFragment : Fragment() {

    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    /* Hilt 사용하기 전
    private lateinit var bookSearchviewModel: BookSearchViewModel
    */

    /* Hilt 사용 후 */
    // by activityViewModels로 ViewModel를 생성한다.
    //private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()
    /*
    viewModel을 각 View마다 나눠 관심사를 분리했으므로 by activityViewModel로 viewModel을 생성하는게 아니라
    그냥 by viewModels로 viewModel을 생성하면 된다.
    lifecycle을 activity로 따를 필요가 없기 때문.
     */
    private val bookViewModel : BookViewModel by viewModels()

    // 이전 프래그먼트로부터 전달받은 데이터 받기(safeArgs)
    // val safeArgs : [전달받은프래그먼트의이름]+[Args] by navArgs()
    // 프래그먼트 전환 시 얻는 아규먼트
    private val args: BookFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SetJavaScriptEnabled") // 웹뷰설정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Hilt 사용 전
        // 메인 액티비티의 ViewModel을 전달 받음.
        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel
         */

        // 프래그먼트 전환 시 받은 아규먼트의 book 데이터를 가지고
        // 웹뷰에 표시
        val book = args.book
        binding.webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(book.url)
        }

        // 뒤로 가기 버튼을 눌렀을 때 fragment stack 제거
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        binding.fabFavorite.setOnClickListener {
            bookViewModel.saveBook(book) // 프래그먼트로 전달 받은 book 아규먼트를 saveBook()에 넣어서 전달
            Snackbar.make(view, "Book has saved", Snackbar.LENGTH_SHORT).show()
        }
    }

    // onPause와 onResume으로 lifecycle에 따른 웹뷰 동작 설정
    override fun onPause() {
        binding.webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}