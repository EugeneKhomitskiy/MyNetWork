package com.example.mynetwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.mynetwork.R
import com.example.mynetwork.databinding.CardUserBinding
import com.example.mynetwork.dto.User

interface UserCallback {
    fun openProfile(user: User)
}

class UserAdapter(private val userCallback: UserCallback) :
    ListAdapter<User, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, userCallback)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val userCallback: UserCallback
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        with(binding) {
            userName.text = user.name

            Glide.with(userAvatar)
                .load("${user.avatar}")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_avatar_default)
                .into(userAvatar)

            viewUser.setOnClickListener {
                userCallback.openProfile(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}