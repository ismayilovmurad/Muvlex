package com.martiandeveloper.muvlex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewSeriesItemBinding
import com.martiandeveloper.muvlex.model.Series
import com.martiandeveloper.muvlex.utils.BASE_URL_POSTER
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.utils.load

class SeriesListAdapter(
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<Series, RecyclerView.ViewHolder>(SeriesDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return SeriesListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_series_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SeriesListViewHolder).bind(context, getItem(position), itemCLickListener)
    }

    class SeriesDiffCallback : DiffUtil.ItemCallback<Series>() {

        override fun areItemsTheSame(oldItem: Series, newItem: Series): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Series, newItem: Series): Boolean {
            return oldItem == newItem
        }

    }

    class SeriesListViewHolder(private val binding: RecyclerviewSeriesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            series: Series?,
            itemClickListener: ItemClickListener
        ) {
            if (series != null)

                binding.let {

                    with(series) {

                        it.name = if (name.check()) name else context.resources.getString(
                            R.string.unknown
                        )

                        it.firstAirDate =
                            if (firstAirDate.check()) firstAirDate!!.split("-")[0] else context.resources.getString(
                                R.string.unknown
                            )

                        it.voteAverage =
                            if (voteAverage.toString().check()) voteAverage!!.toString()
                                .split("-")[0] else context.resources.getString(
                                R.string.unknown
                            )

                        it.recyclerviewSeriesItemPosterIV.load(
                            context,
                            if (posterPath.check()) "${BASE_URL_POSTER}${posterPath}" else null
                        )

                        itemView.setOnClickListener {
                            itemClickListener.onItemClick(this)
                        }

                    }

                    it.executePendingBindings()

                }

        }

    }

    interface ItemClickListener {
        fun onItemClick(series: Series)
    }

}
