package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.databinding.FragmentSignInBinding
import com.example.mynetwork.viewmodel.SignInViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignIn.setOnClickListener {
            viewModel.updateUser(
                binding.textFieldLogin.editText?.text.toString(),
                binding.textFieldPassword.editText?.text.toString()
            )
        }

        binding.textFieldPassword.setErrorIconOnClickListener {
            binding.textFieldPassword.error = null
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.errorLogin) {
                binding.textFieldPassword.error = getString(R.string.error_login)
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).visibility =
            View.GONE

        return binding.root
    }

    override fun onDestroy() {
        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).visibility =
            View.VISIBLE
        super.onDestroy()
    }
}