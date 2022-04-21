package com.example.mynetwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.mynetwork.R
import com.example.mynetwork.databinding.CardEventBinding
import com.example.mynetwork.dto.Event
import com.example.mynetwork.extension.formatToDate

interface OnEventInteractionListener {
    fun openEvent(event: Event)
    fun onRemove(event: Event)
    fun onEdit(event: Event)
    fun onLike(event: Event)
    fun onParticipate(event: Event)
    fun onOpenSpeakers(event: Event)
    fun onOpenLikeOwners(event: Event)
    fun onOpenParticipants(event: Event)
}

class EventAdapter(private val onEventInteractionListener: OnEventInteractionListener) :
    PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onEventInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        event?.let { holder.bind(it) }
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onEventInteractionListener: OnEventInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        with(binding) {
            author.text = event.author
            published.text = formatToDate(event.published)
            dateTime.text = formatToDate(event.datetime)
            content.text = event.content
            like.isChecked = event.likedByMe
            participate.isChecked = event.participatedByMe
            likers.text = event.likeOwnerIds.count().toString()

            Glide.with(avatar)
                .load("${event.authorAvatar}")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_avatar_default)
                .into(avatar)

            binding.like.setOnClickListener {
                onEventInteractionListener.onLike(event)
            }

            binding.speakers.setOnClickListener {
                onEventInteractionListener.onOpenSpeakers(event)
            }

            binding.likers.setOnClickListener {
                onEventInteractionListener.onOpenLikeOwners(event)
            }

            binding.participants.setOnClickListener {
                onEventInteractionListener.onOpenParticipants(event)
            }

            binding.participate.setOnClickListener {
                onEventInteractionListener.onParticipate(event)
            }

            menu.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, event.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onEventInteractionListener.onRemove(event)
                                true
                            }
                            R.id.edit -> {
                                onEventInteractionListener.onEdit(event)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}