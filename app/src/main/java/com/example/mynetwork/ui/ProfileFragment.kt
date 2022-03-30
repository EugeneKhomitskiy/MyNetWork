package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.databinding.FragmentProfileBinding
import com.example.mynetwork.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


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

        with(binding) {
            if (authViewModel.authenticated) buttonInOut.visibility = View.GONE
            buttonInOut.setOnClickListener {
                findNavController().navigate(R.id.signInFragment)
            }
        }

        return binding.root
    }
}