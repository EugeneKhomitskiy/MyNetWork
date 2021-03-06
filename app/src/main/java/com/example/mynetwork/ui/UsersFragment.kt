package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.adapter.OnUserInteractionListener
import com.example.mynetwork.adapter.UserAdapter
import com.example.mynetwork.databinding.FragmentUsersBinding
import com.example.mynetwork.dto.User
import com.example.mynetwork.viewmodel.EventViewModel
import com.example.mynetwork.viewmodel.PostViewModel
import com.example.mynetwork.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(
            inflater,
            container,
            false
        )

        val open = arguments?.getString("open")

        val adapter = UserAdapter(object : OnUserInteractionListener {
            override fun openProfile(user: User) {
                when (open) {
                    "mention" -> {
                        postViewModel.changeMentionIds(user.id)
                        postViewModel.save()
                        findNavController().navigateUp()
                    }
                    "speaker" -> {
                        eventViewModel.setSpeaker(user.id)
                        findNavController().navigateUp()
                    }
                    else -> {
                        userViewModel.getUserById(user.id)
                        val bundle = Bundle().apply {
                            putLong("id", user.id)
                            putString("avatar", user.avatar)
                            putString("name", user.name)
                        }
                        findNavController().apply {
                            this.popBackStack(R.id.navigation_users, true)
                            this.navigate(R.id.navigation_profile, bundle)
                        }
                    }
                }
            }
        })

        binding.list.adapter = adapter

        userViewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        userViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT).show()
                }
            }
            binding.progress.isVisible = it.loading
        }

        return binding.root
    }
}