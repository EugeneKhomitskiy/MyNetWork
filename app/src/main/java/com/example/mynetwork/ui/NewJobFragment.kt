package com.example.mynetwork.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.databinding.FragmentNewJobBinding
import com.example.mynetwork.extension.dateToEpochSec
import com.example.mynetwork.extension.epochSecToDate
import com.example.mynetwork.extension.pickDate
import com.example.mynetwork.util.AndroidUtils
import com.example.mynetwork.viewmodel.JobViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val jobViewModel: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )

        val name = arguments?.getString("name")
        val position = arguments?.getString("position")
        val start = arguments?.getLong("start")
        val finish = arguments?.getLong("finish")

        binding.name.setText(name)
        binding.position.setText(position)
        binding.start.setText(start?.let { epochSecToDate(it) })
        binding.finish.setText(
            if (finish == 0L) "" else finish?.let { epochSecToDate(it) }
        )

        binding.save.setOnClickListener {
            binding.let {
                if (it.name.text.isNullOrBlank() || it.position.text.isNullOrBlank() ||
                    it.start.text.isNullOrBlank()
                ) {
                    Toast.makeText(
                        activity,
                        R.string.error_required_fields,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    jobViewModel.change(
                        it.name.text.toString(),
                        it.position.text.toString(),
                        dateToEpochSec(it.start.text.toString())!!,
                        dateToEpochSec(it.finish.text.toString())
                    )
                    jobViewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                }
            }
        }

        binding.start.setOnClickListener {
            it.pickDate(binding.textFieldStart.editText, context)
        }

        binding.finish.setOnClickListener {
            it.pickDate(binding.textFieldFinish.editText, context)
        }

        jobViewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}