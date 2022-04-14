package com.example.mynetwork.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.mynetwork.adapter.PostAdapter
import com.example.mynetwork.adapter.PostCallback
import com.example.mynetwork.databinding.FragmentWallBinding
import com.example.mynetwork.dto.Post
import com.example.mynetwork.viewmodel.WallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WallFragment : Fragment() {

    private val wallViewModel: WallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWallBinding.inflate(
            inflater, container, false
        )

        val adapter = PostAdapter(object : PostCallback {
            override fun openPost(post: Post) {
                TODO("Not yet implemented")
            }
        })

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            wallViewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        wallViewModel.clearWall()
    }
}