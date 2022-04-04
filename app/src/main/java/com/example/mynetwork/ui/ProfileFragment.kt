package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mynetwork.R
import com.example.mynetwork.adapter.ViewPagerAdapter
import com.example.mynetwork.databinding.FragmentProfileBinding
import com.example.mynetwork.viewmodel.AuthViewModel
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

    private val authViewModel: AuthViewModel by viewModels()

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

        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()

        authViewModel.data.observe(viewLifecycleOwner) {
            if (!authViewModel.authenticated) viewPager.adapter = ViewPagerAdapter(this)
        }

        return binding.root
    }
}