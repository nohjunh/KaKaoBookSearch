package com.nohjunh.booksearchapp.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nohjunh.booksearchapp.R
import com.nohjunh.booksearchapp.data.repository.BookSearchRepositoryImpl
import com.nohjunh.booksearchapp.databinding.ActivityMainBinding
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // 액티비티에서 ViewModel을 초기화 시켜줌.
    private val bookSearchRepository = BookSearchRepositoryImpl()

    // saveStateOwner는 this@MainActivity
    private val factory = BookSearchViewModelProviderFactory(bookSearchRepository, this)
    val bookSearchViewModel: BookSearchViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupJetPackNavigation()

        /*
        //앱이 처음 실행되었을 때만 화면에 searchFragment가 보이도록 고정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.searchFragment
        }
        */
    }

    private fun setupJetPackNavigation() {
        // navigation 작업할 때 각 component들이 bottom_navigation_menu와 main_nav에서의 ID가 같아야 함.
        val bottomNavView = binding.bottomNavigationView
        val navController =
            findNavController(R.id.booksearch_nav_host_fragment)  // 네비게이션 컨트롤러 인스턴스를 취득
        bottomNavView.setupWithNavController(navController)
    }

}