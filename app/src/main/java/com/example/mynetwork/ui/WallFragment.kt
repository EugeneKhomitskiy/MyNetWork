package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mynetwork.R
import com.example.mynetwork.adapter.OnPostInteractionListener
import com.example.mynetwork.adapter.PostAdapter
import com.example.mynetwork.databinding.FragmentWallBinding
import com.example.mynetwork.dto.Post
import com.example.mynetwork.viewmodel.AuthViewModel
import com.example.mynetwork.viewmodel.PostViewModel
import com.example.mynetwork.viewmodel.UserViewModel
import com.example.mynetwork.viewmodel.WallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WallFragment : Fragment() {

    private val wallViewModel: WallViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWallBinding.inflate(
            inflater, container, false
        )

        val adapter = PostAdapter(object : OnPostInteractionListener {
            override fun openPost(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onRemove(post: Post) {
                postViewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                postViewModel.edit(post)
                val bundle = Bundle().apply { putString("content", post.content) }
                findNavController().navigate(R.id.newPostFragment, bundle)
            }

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likedByMe) postViewModel.likeById(post.id) else postViewModel.dislikeById(post.id)
                } else {
                    Toast.makeText(
                        activity,
                        R.string.error_auth,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onMention(post: Post) {
                if (authViewModel.authenticated) {
                    postViewModel.edit(post)
                    val bundle = Bundle().apply { putString("open", "mention") }
                    findNavController().navigate(R.id.navigation_users, bundle)
                } else {
                    Toast.makeText(
                        activity,
                        R.string.error_auth,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        val id = parentFragment?.arguments?.getLong("id")

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            if (id != null) {
                wallViewModel.userWall(id).collectLatest(adapter::submitData)
            }
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
}