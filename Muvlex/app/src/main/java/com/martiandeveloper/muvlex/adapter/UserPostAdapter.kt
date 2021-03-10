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
import com.martiandeveloper.muvlex.databinding.RecyclerviewPostItemBinding
import com.martiandeveloper.muvlex.model.Post
import com.martiandeveloper.muvlex.utils.BASE_URL_POSTER
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.utils.load
import java.text.ParseException

class UserPostAdapter(
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return PostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_post_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PostViewHolder).bind(context, getItem(position), itemCLickListener)
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    class PostViewHolder(private val binding: RecyclerviewPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            post: Post?,
            itemClickListener: ItemClickListener
        ) {
            if (post != null)

                binding.let {

                    with(post) {

                        it.title =
                            if (title.check()) title else context.resources.getString(R.string.unknown)

                        it.rating = if (rating.check()) rating!!.toFloat() else 0.0F

                        it.time =
                            if (time != null) getPrettyTime(time) else context.resources.getString(R.string.unknown)

                        it.review =
                            if (review.check()) review else context.resources.getString(R.string.unknown)

                        binding.recyclerviewPostItemPosterIV.load(
                            context,
                            if (posterPath.check()) BASE_URL_POSTER + posterPath else null
                        )

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
        fun onItemClick(post: Post)
    }

}
