package com.nohjunh.booksearchapp.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nohjunh.booksearchapp.R
import com.nohjunh.booksearchapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupBottomNavigationView()

        //앱이 처음 실행되었을 때만 화면에 searchFragment가 보이도록 고정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.searchFragment
        }
    }

    private fun setupBottomNavigationView() {
        // navigation 작업할 때 각 component들이 bottom_navigation_menu와 main_nav에서의 ID가 같아야 함.
        val bottomNavView = binding.bottomNavigationView
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavView.setupWithNavController(navController)
    }
}