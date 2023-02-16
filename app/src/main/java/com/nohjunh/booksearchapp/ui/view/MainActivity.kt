package com.nohjunh.booksearchapp.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nohjunh.booksearchapp.R
import com.nohjunh.booksearchapp.data.db.BookSearchDatabase
import com.nohjunh.booksearchapp.data.repository.BookSearchRepositoryImpl
import com.nohjunh.booksearchapp.databinding.ActivityMainBinding
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupJetPackNavigation()

        // 메인 액티비티에서 Database를 구현해서 넘겨줌
        val database = BookSearchDatabase.getInstance(this)

        // 메인 액티비티에서 ViewModel을 초기화 시켜줌.
        val bookSearchRepository = BookSearchRepositoryImpl(database)

        // saveStateOwner는 this@MainActivity
        val factory = BookSearchViewModelProviderFactory(bookSearchRepository, this)
        bookSearchViewModel = ViewModelProvider(this, factory)[BookSearchViewModel::class.java]

        /*
        //앱이 처음 실행되었을 때만 화면에 searchFragment가 보이도록 고정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.searchFragment
        }
        */
    }

    private fun setupJetPackNavigation() {
        // navigation 작업할 때 각 component들이 bottom_navigation_menu와 main_nav에서의 ID가 같아야 함.
        val host = supportFragmentManager
            .findFragmentById(R.id.booksearch_nav_host_fragment) as NavHostFragment? ?: return
        navController = host.navController // 네비게이션 컨트롤러 인스턴스 취득
        binding.bottomNavigationView.setupWithNavController(navController)
    }

}