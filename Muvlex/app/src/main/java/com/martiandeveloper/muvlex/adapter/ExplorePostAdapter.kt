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
import com.martiandeveloper.muvlex.databinding.RecyclerviewExplorePostItemBinding
import com.martiandeveloper.muvlex.model.ExplorePost
import com.martiandeveloper.muvlex.utils.load
import java.text.ParseException


class ExplorePostAdapter(private val itemCLickListener: ItemClickListener) :
    PagingDataAdapter<ExplorePost, ExplorePostAdapter.ExplorePostViewHolder>(ExplorePostDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExplorePostViewHolder {
        context = parent.context

        return ExplorePostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_explore_post_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExplorePostViewHolder, position: Int) {
        holder.bind(context, getItem(position), itemCLickListener)
    }

    class ExplorePostDiffCallback : DiffUtil.ItemCallback<ExplorePost>() {

        override fun areItemsTheSame(oldItem: ExplorePost, newItem: ExplorePost): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: ExplorePost, newItem: ExplorePost): Boolean {
            return oldItem == newItem
        }

    }

    class ExplorePostViewHolder(private val binding: RecyclerviewExplorePostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            explorePost: ExplorePost?,
            itemClickListener: ItemClickListener
        ) {
            if (explorePost != null)

                binding.let {

                    with(explorePost) {

                        it.title =
                            if (explorePost.title != null) title else context.resources.getString(R.string.unknown)
                        it.star = if (explorePost.rating != null) rating!!.toFloat() else 1F
                        it.time = if (explorePost.time != null) getPrettyTime(time!!) else "-"
                        it.review =
                            if (explorePost.review != null) review else context.resources.getString(
                                R.string.unknown
                            )

                        binding.recyclerviewExplorePostItemPosterIV.load(context, posterPath)

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
        fun onItemClick(profilePost: ExplorePost)
    }

}
