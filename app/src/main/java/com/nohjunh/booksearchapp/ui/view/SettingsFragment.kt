package com.nohjunh.booksearchapp.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.nohjunh.booksearchapp.R
import com.nohjunh.booksearchapp.databinding.FragmentSettingsBinding
import com.nohjunh.booksearchapp.ui.viewmodel.SettingsViewModel
import com.nohjunh.booksearchapp.util.Sort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    /* Hilt 사용전
    private lateinit var bookSearchViewModel: BookSearchViewModel
    */


    /* Hilt 사용 후 */
    // by activityViewModels로 ViewModel를 생성한다.
    /*
    viewModel을 각 View마다 나눠 관심사를 분리했으므로 by activityViewModel로 viewModel을 생성하는게 아니라
    그냥 by viewModels로 viewModel을 생성하면 된다.
    lifecycle을 activity로 따를 필요가 없기 때문.

    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()
    */

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Hilt 사용 전
        // 메인 액티비티의 ViewModel을 전달 받음.
        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel
        */

        saveSettings()
        loadSettings()
        showWorkStatus()
    }

    // 체크된 버튼을 확인하고 해당되는 SORT enum값을 받아와서
    // 저장함.
    private fun saveSettings() {
        binding.rgSort.setOnCheckedChangeListener { _, checkedId ->
            val value = when (checkedId) {
                R.id.rb_accuracy -> Sort.ACCURACY.value
                R.id.rb_latest -> Sort.LATEST.value
                else -> return@setOnCheckedChangeListener
            }
            settingsViewModel.saveSortMode(value)
        }
        binding.swCacheDelete.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveCacheDeleteMode(isChecked)
            if (isChecked) {
                settingsViewModel.setWork()
            } else {
                settingsViewModel.deleteWork()
            }
        }
    }

    // 불러온 값을 확인하고 라디오버튼에 반영.
    private fun loadSettings() {
        lifecycleScope.launch {
            val buttonId = when (settingsViewModel.getSortMode()) {
                Sort.ACCURACY.value -> R.id.rb_accuracy
                Sort.LATEST.value -> R.id.rb_latest
                else -> return@launch
            }
            binding.rgSort.check(buttonId)
        }

        lifecycleScope.launch {
            val mode = settingsViewModel.getCacheDeleteMode()
            binding.swCacheDelete.isChecked = mode
            }
    }

    private fun showWorkStatus() {
    // getWorkStatus()이 반환형이 LiveData이므로 observe로 구독 가능
        settingsViewModel.getWorkStatus().observe(viewLifecycleOwner, Observer { workInfo ->
        Log.d("WorkManager", workInfo.toString())
    // 초기에는 값이 존재하지 않기에 isEmpty로 분기를 나눔
        if (workInfo.isEmpty()) {
            binding.tvWorkStatus.text = "No wokers"
        } else {
            // workInfo는 [0]번째로 가져오면 된다. Log확인해보면 됨.
            binding.tvWorkStatus.text = workInfo[0].state.toString()
            }
        })
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}