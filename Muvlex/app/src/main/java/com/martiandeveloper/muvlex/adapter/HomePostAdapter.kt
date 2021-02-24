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
import com.martiandeveloper.muvlex.databinding.RecyclerviewHomePostItemBinding
import com.martiandeveloper.muvlex.model.HomePost
import com.martiandeveloper.muvlex.utils.load
import java.text.ParseException


class HomePostAdapter(private val itemCLickListener: ItemClickListener) :
    PagingDataAdapter<HomePost, HomePostAdapter.HomePostViewHolder>(HomePostDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        context = parent.context

        return HomePostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_home_post_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        holder.bind(context, getItem(position), itemCLickListener)
    }

    class HomePostDiffCallback : DiffUtil.ItemCallback<HomePost>() {

        override fun areItemsTheSame(oldItem: HomePost, newItem: HomePost): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: HomePost, newItem: HomePost): Boolean {
            return oldItem == newItem
        }

    }

    class HomePostViewHolder(private val binding: RecyclerviewHomePostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            homePost: HomePost?,
            itemClickListener: ItemClickListener
        ) {
            if (homePost != null)

                binding.let {

                    with(homePost) {

                        it.title =
                            if (homePost.title != null) title else context.resources.getString(R.string.unknown)
                        it.rate = if (homePost.star != null) star!!.toFloat() else 1F
                        it.time = if (homePost.time != null) getPrettyTime(time!!) else "-"
                        it.review =
                            if (homePost.review != null) review else context.resources.getString(
                                R.string.unknown
                            )

                        binding.recyclerviewHomePostItemPosterIV.load(context, posterPath)

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
        fun onItemClick(homePost: HomePost)
    }

}
