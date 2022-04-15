package com.example.mynetwork.adapter

import android.annotation.SuppressLint
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
import com.example.mynetwork.databinding.CardPostBinding
import com.example.mynetwork.dto.Post
import com.example.mynetwork.enumeration.AttachmentType
import com.example.mynetwork.extension.formatDate

interface PostCallback {
    fun openPost(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
}

class PostAdapter(private val postCallback: PostCallback) :
    PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, postCallback)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val postCallback: PostCallback
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("NewApi")
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = formatDate(post.published)
            content.text = post.content

            Glide.with(avatar)
                .load("${post.authorAvatar}")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_avatar_default)
                .into(avatar)

            post.attachment?.apply {
                when (AttachmentType.values().first()) {
                    AttachmentType.IMAGE -> {
                        Glide.with(imageAttachment)
                            .load(this.url)
                            .placeholder(R.drawable.ic_loading_100dp)
                            .error(R.drawable.ic_error_100dp)
                            .timeout(10_000)
                            .into(imageAttachment)
                    }
                    AttachmentType.VIDEO -> TODO()
                    AttachmentType.AUDIO -> TODO()
                }
            }

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                postCallback.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                postCallback.onEdit(post)
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

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}