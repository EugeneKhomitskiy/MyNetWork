package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.adapter.JobAdapter
import com.example.mynetwork.adapter.OnJobInteractionListener
import com.example.mynetwork.databinding.FragmentJobsBinding
import com.example.mynetwork.dto.Job
import com.example.mynetwork.viewmodel.JobViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class JobsFragment : Fragment() {

    private val jobViewModel: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentJobsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = JobAdapter(object : OnJobInteractionListener {
            override fun onRemove(job: Job) {
                jobViewModel.removeById(job.id)
            }

            override fun onEdit(job: Job) {
                jobViewModel.edit(job)
                val bundle = Bundle().apply {
                    putString("name", job.name)
                    putString("position", job.position)
                    putLong("start", job.start)
                    job.finish?.let { putLong("finish", it) }
                }
                findNavController().navigate(R.id.newJobFragment, bundle)
            }
        })

        val id = parentFragment?.arguments?.getLong("id")

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            if (id != null) {
                jobViewModel.setId(id)
                jobViewModel.loadJobs(id)
            }
            jobViewModel.data.collectLatest(adapter::submitList)
        }

        return binding.root
    }
}