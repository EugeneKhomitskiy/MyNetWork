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
import com.example.mynetwork.databinding.FragmentSignUpBinding
import com.example.mynetwork.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignUp.setOnClickListener {
            if (binding.textFieldPassword.editText?.text.toString() == binding.textFieldRepeatPassword.editText?.text.toString()) {
                viewModel.registerUser(
                    binding.textFieldLogin.editText?.text.toString(),
                    binding.textFieldPassword.editText?.text.toString(),
                    binding.textFieldName.editText?.text.toString()
                )
            } else binding.textFieldRepeatPassword.error = getString(R.string.error_repeat_password)
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigate(R.id.action_signUpFragment_to_appActivity)
        }

        return binding.root
    }
}