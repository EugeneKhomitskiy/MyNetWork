package com.example.mynetwork.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
import java.lang.Exception

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WallFragment : Fragment() {

    private val wallViewModel: WallViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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
                val bundle = Bundle().apply {
                    putString("content", post.content)
                    post.coords?.lat?.let { putDouble("lat", it) }
                    post.coords?.long?.let { putDouble("lng", it) }
                }
                findNavController().navigate(R.id.newPostFragment, bundle)
            }

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likedByMe) postViewModel.likeById(post.id) else postViewModel.dislikeById(
                        post.id
                    )
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

            override fun onOpenMentions(post: Post) {
                userViewModel.getUsersIds(post.mentionIds)
                if (post.mentionIds.isEmpty()) {
                    Toast.makeText(context, R.string.empty_mentions, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.bottomSheetFragment)
                }
            }

            override fun onOpenLikeOwners(post: Post) {
                userViewModel.getUsersIds(post.likeOwnerIds)
                if (post.likeOwnerIds.isEmpty()) {
                    Toast.makeText(context, R.string.empty_like_owners, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.bottomSheetFragment)
                }
            }

            override fun onPlayAudio(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "audio/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPlayVideo(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "video/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onOpenMap(post: Post) {
                val bundle = Bundle().apply {
                    post.coords?.lat?.let { putDouble("lat", it) }
                    post.coords?.long?.let { putDouble("lng", it) }
                }
                findNavController().navigate(R.id.mapFragment, bundle)
            }

            override fun onOpenImageAttachment(post: Post) {
                val bundle = Bundle().apply {
                    putString("url", post.attachment?.url)
                }
                findNavController().navigate(R.id.imageFragment, bundle)
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
                binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                binding.emptyText.isVisible = adapter.itemCount < 1
            }
        }

        postViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT).show()
                }
            }
            binding.progress.isVisible = it.loading
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}