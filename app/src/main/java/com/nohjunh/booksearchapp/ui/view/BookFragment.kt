package com.nohjunh.booksearchapp.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.nohjunh.booksearchapp.databinding.FragmentBookBinding

class BookFragment : Fragment() {

    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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