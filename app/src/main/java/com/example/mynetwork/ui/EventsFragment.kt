package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.mynetwork.adapter.EventAdapter
import com.example.mynetwork.adapter.OnEventInteractionListener
import com.example.mynetwork.databinding.FragmentEventsBinding
import com.example.mynetwork.dto.Event
import com.example.mynetwork.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EventsFragment : Fragment() {

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = EventAdapter(object : OnEventInteractionListener {
            override fun openEvent(event: Event) {
                TODO("Not yet implemented")
            }

            override fun onRemove(event: Event) {
                eventViewModel.removeById(event.id)
            }
        })

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            eventViewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}