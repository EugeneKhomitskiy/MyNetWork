package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.adapter.OnUserInteractionListener
import com.example.mynetwork.adapter.UserAdapter
import com.example.mynetwork.databinding.FragmentBottomSheetBinding
import com.example.mynetwork.dto.User
import com.example.mynetwork.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = UserAdapter(object : OnUserInteractionListener {
            override fun openProfile(user: User) {
                userViewModel.getUserById(user.id)
                val bundle = Bundle().apply {
                    putLong("id", user.id)
                    putString("avatar", user.avatar)
                    putString("name", user.name)
                }
                findNavController().apply {
                    this.popBackStack(R.id.navigation_main, true) // TODO Переделать, по кнопке назад выходит из приложения
                    this.navigate(R.id.navigation_profile, bundle)
                }
            }
        })

        binding.list.adapter = adapter

        userViewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it.filter {  user ->
                userViewModel.userIds.value!!.contains(user.id)
            })
        }

        return binding.root
    }
}