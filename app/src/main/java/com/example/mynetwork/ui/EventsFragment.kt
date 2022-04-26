package com.example.mynetwork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mynetwork.R
import com.example.mynetwork.adapter.EventAdapter
import com.example.mynetwork.adapter.OnEventInteractionListener
import com.example.mynetwork.databinding.FragmentEventsBinding
import com.example.mynetwork.dto.Event
import com.example.mynetwork.viewmodel.AuthViewModel
import com.example.mynetwork.viewmodel.EventViewModel
import com.example.mynetwork.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EventsFragment : Fragment() {

    private val eventViewModel: EventViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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

            override fun onEdit(event: Event) {
                eventViewModel.edit(event)
                val bundle = Bundle().apply {
                    putString("content", event.content)
                    putString("dateTime", event.datetime)
                }
                findNavController().navigate(R.id.newEventFragment, bundle)
            }

            override fun onLike(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.likedByMe) eventViewModel.likeById(event.id)
                    else eventViewModel.dislikeById(event.id)
                } else {
                    Toast.makeText(
                        activity,
                        R.string.error_auth,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onParticipate(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.participatedByMe) eventViewModel.participate(event.id)
                    else eventViewModel.notParticipate(event.id)
                } else {
                    Toast.makeText(
                        activity,
                        R.string.error_auth,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onOpenSpeakers(event: Event) {
                userViewModel.getUsersIds(event.speakerIds)
                if (event.speakerIds.isEmpty()) {
                    Toast.makeText(context, R.string.empty_speakers, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_navigation_events_to_bottomSheetFragment)
                }
            }

            override fun onOpenLikeOwners(event: Event) {
                userViewModel.getUsersIds(event.likeOwnerIds)
                if (event.likeOwnerIds.isEmpty()) {
                    Toast.makeText(context, R.string.empty_like_owners, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_navigation_events_to_bottomSheetFragment)
                }
            }

            override fun onOpenParticipants(event: Event) {
                userViewModel.getUsersIds(event.participantsIds)
                if (event.participantsIds.isEmpty()) {
                    Toast.makeText(context, R.string.empty_participants, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_navigation_events_to_bottomSheetFragment)
                }
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

        eventViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}