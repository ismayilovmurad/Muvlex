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
import com.martiandeveloper.muvlex.databinding.RecyclerviewProfilePostItemBinding
import com.martiandeveloper.muvlex.model.ProfilePost
import com.martiandeveloper.muvlex.utils.load
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.days


class ProfilePostAdapter(private val itemCLickListener: ItemClickListener) :
    PagingDataAdapter<ProfilePost, ProfilePostAdapter.ProfilePostViewHolder>(ProfilePostDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        context = parent.context

        return ProfilePostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_profile_post_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        holder.bind(context, getItem(position), itemCLickListener)
    }

    class ProfilePostDiffCallback : DiffUtil.ItemCallback<ProfilePost>() {

        override fun areItemsTheSame(oldItem: ProfilePost, newItem: ProfilePost): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: ProfilePost, newItem: ProfilePost): Boolean {
            return oldItem == newItem
        }

    }

    class ProfilePostViewHolder(private val binding: RecyclerviewProfilePostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            profilePost: ProfilePost?,
            itemClickListener: ItemClickListener
        ) {
            if (profilePost != null)

                binding.let {

                    with(profilePost) {

                        it.title =
                            if (profilePost.title != null) title else context.resources.getString(R.string.unknown)
                        it.star = if (profilePost.star != null) star!!.toFloat() else 1F
                        it.time = if (profilePost.time != null) getPrettyTime(time!!) else "-"
                        it.review =
                            if (profilePost.review != null) review else context.resources.getString(
                                R.string.unknown
                            )

                        binding.recyclerviewMyPostItemPosterIV.load(context, posterPath)

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
        fun onItemClick(profilePost: ProfilePost)
    }

}
