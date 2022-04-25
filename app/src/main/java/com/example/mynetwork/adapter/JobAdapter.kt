package com.example.mynetwork.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynetwork.R
import com.example.mynetwork.databinding.CardJobBinding
import com.example.mynetwork.dto.Job
import com.example.mynetwork.extension.epochSecToDate

interface OnJobInteractionListener {
    fun onRemove(job: Job)
    fun onEdit(job: Job)
}

class JobAdapter(private val onJobInteractionListener: OnJobInteractionListener) :
    ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(parent.context, binding, onJobInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }
}

class JobViewHolder(
    private val context: Context,
    private val binding: CardJobBinding,
    private val onJobInteractionListener: OnJobInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(job: Job) {
        with(binding) {
            name.text = "${context.getString(R.string.company)} ${job.name}"
            position.text = "${context.getString(R.string.my_position)} ${job.position}"
            start.text = "${context.getString(R.string.from)} ${epochSecToDate(job.start).substring(0, 7)} "
            finish.text = if (job.finish == null) {
                "${context.getString(R.string.to)} ${context.getString(R.string.now)}"
            } else {
                "${context.getString(R.string.to)} ${epochSecToDate(job.finish).substring(0, 7)}"
            }

            menu.visibility = if (job.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, job.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onJobInteractionListener.onRemove(job)
                                true
                            }
                            R.id.edit -> {
                                onJobInteractionListener.onEdit(job)
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

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}