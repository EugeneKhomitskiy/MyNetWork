package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.mynetwork.R
import com.example.mynetwork.adapter.ViewPagerAdapter
import com.example.mynetwork.databinding.FragmentProfileBinding
import com.example.mynetwork.dto.Post
import com.example.mynetwork.viewmodel.AuthViewModel
import com.example.mynetwork.viewmodel.PostViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val TAB_TITLES = arrayOf(
    R.string.title_posts,
    R.string.title_events,
    R.string.title_jobs
)

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private var isVisibleGroupFab = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val id = arguments?.getLong("id")
        val avatar = arguments?.getString("avatar")
        val name = arguments?.getString("name")

        (activity as AppCompatActivity).supportActionBar?.title = name

        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()

        with(binding) {
            userName.text = name

            Glide.with(userAvatar)
                .load("$avatar")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_avatar_default)
                .into(userAvatar)
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authenticated && id == it.id) {
                binding.fab.visibility = View.VISIBLE
            }
        }

        binding.fab.setOnClickListener {
            if (!isVisibleGroupFab) {
                binding.fab.setImageResource(R.drawable.ic_baseline_close_24)
                binding.groupFab.visibility = View.VISIBLE
            } else {
                binding.fab.setImageResource(R.drawable.ic_baseline_add_24)
                binding.groupFab.visibility = View.GONE
            }
            isVisibleGroupFab = !isVisibleGroupFab
        }

        binding.fabAddPost.setOnClickListener {
            postViewModel.edit(Post.empty)
            findNavController().navigate(R.id.action_navigation_profile_to_newPostFragment)
        }

        binding.fabAddEvent.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_newEventFragment)
        }

        return binding.root
    }
}