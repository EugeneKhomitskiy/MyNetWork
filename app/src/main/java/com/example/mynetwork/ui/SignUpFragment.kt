package com.example.mynetwork.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.databinding.FragmentSignUpBinding
import com.example.mynetwork.viewmodel.SignUpViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
            binding.let {
                if (it.name.text.isNullOrBlank() || it.login.text.isNullOrBlank() ||
                    it.password.text.isNullOrBlank() || it.repeatPassword.text.isNullOrBlank()
                ) {
                    Toast.makeText(
                        activity,
                        R.string.error_required_fields,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (binding.textFieldPassword.editText?.text.toString() == binding.textFieldRepeatPassword.editText?.text.toString()) {
                        viewModel.registerUser(
                            binding.textFieldLogin.editText?.text.toString(),
                            binding.textFieldPassword.editText?.text.toString(),
                            binding.textFieldName.editText?.text.toString()
                        )
                    } else binding.textFieldRepeatPassword.error =
                        getString(R.string.error_repeat_password)
                }
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri)
                    }
                }
            }

        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigate(R.id.action_signUpFragment_to_appActivity)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                return@observe
            }
            binding.profileImage.setImageURI(it.uri)
        }

        return binding.root
    }
}