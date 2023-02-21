package com.nohjunh.booksearchapp.ui.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.nohjunh.booksearchapp.R
import com.nohjunh.booksearchapp.data.db.BookSearchDatabase
import com.nohjunh.booksearchapp.data.repository.BookSearchRepositoryImpl
import com.nohjunh.booksearchapp.databinding.ActivityMainBinding
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.nohjunh.booksearchapp.ui.viewmodel.BookSearchViewModelProviderFactory
import com.nohjunh.booksearchapp.util.Constants.DATASTORE_NAME

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var navController: NavController

    // DataStore singleton객체는 이런 식으로 만듬.
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)
    private val workManager = WorkManager.getInstance(application)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupJetPackNavigation()

        // 메인 액티비티에서 Database를 구현해서 넘겨줌
        val database = BookSearchDatabase.getInstance(this)

        // 메인 액티비티에서 ViewModel을 초기화 시켜줌.
        val bookSearchRepository = BookSearchRepositoryImpl(database, dataStore)

        /*
        ViewModel의 생성자에 파라미터가 필요할 경우
        ViewModelProvider.Factory, SavedStateViewModelFactory, AbstractSavedStateViewModelFactory
        등을 생성해서 초기화 해주어야 했습니다.
        매번 팩토리를 생성해야 되고 필요한 파라미터를 일일히 관리해주어야 하는 번거로운 일을
        Koin과 Hilt가 위와 같은 방법으로 해결해 줄 수 있습니다.
         */
        // saveStateOwner는 this@MainActivity
        val factory = BookSearchViewModelProviderFactory(bookSearchRepository, workManager, this)
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