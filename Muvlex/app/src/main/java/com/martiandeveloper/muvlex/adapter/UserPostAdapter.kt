package com.martiandeveloper.muvlex.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewUserPostItemBinding
import com.martiandeveloper.muvlex.model.UserPost
import com.martiandeveloper.muvlex.utils.load
import timber.log.Timber
import java.text.ParseException


class UserPostAdapter(private val itemCLickListener: ItemClickListener) :
    PagingDataAdapter<UserPost, UserPostAdapter.UserPostViewHolder>(UserPostDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        context = parent.context

        return UserPostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_user_post_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        holder.bind(context, getItem(position), itemCLickListener)
    }

    class UserPostDiffCallback : DiffUtil.ItemCallback<UserPost>() {

        override fun areItemsTheSame(oldItem: UserPost, newItem: UserPost): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: UserPost, newItem: UserPost): Boolean {
            return oldItem == newItem
        }

    }

    class UserPostViewHolder(private val binding: RecyclerviewUserPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            userPost: UserPost?,
            itemClickListener: ItemClickListener
        ) {
            if (userPost != null)

                binding.let {

                    with(userPost) {

                        Timber.d("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww: ${userPost.review}")

                        it.title =
                            if (userPost.title != null) title else context.resources.getString(R.string.unknown)
                        it.rating = if (userPost.rating != null) rating!!.toFloat() else 1F
                        it.time = if (userPost.time != null) getPrettyTime(time!!) else "-"
                        it.review =
                            if (userPost.review != null) review else context.resources.getString(
                                R.string.unknown
                            )

                        binding.recyclerviewUserPostItemPosterIV.load(context, posterPath)

                        itemView.setOnClickListener {
                            itemClickListener.onItemClick(this)
                        }

                    }

                    it.executePendingBindings()
                }

        }

        private fun getPrettyTime(time: Timestamp): String {

            return try {
                val currentTime = System.currentTimeMillis()
                val prettyTime = DateUtils.getRelativeTimeSpanString(
                    time.toDate().time,
                    currentTime,
                    DateUtils.MINUTE_IN_MILLIS
                )
                prettyTime.toString()
            } catch (e: ParseException) {
                binding.root.context.resources.getString(R.string.unknown)
            }

        }

    }

    interface ItemClickListener {
        fun onItemClick(UserPost: UserPost)
    }

}
