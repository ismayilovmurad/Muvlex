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
import com.martiandeveloper.muvlex.utils.loadImage

class SeriesListAdapter(
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<Series, RecyclerView.ViewHolder>(SeriesDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        val binding: RecyclerviewSeriesItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.recyclerview_series_item,
            parent,
            false
        )

        return SeriesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SeriesListViewHolder).bind(getItem(position), context, itemCLickListener)
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
            series: Series?,
            context: Context,
            itemClickListener: ItemClickListener
        ) {

            if (series != null) {

                binding.let {

                    if (series.name != null && series.name != "null" && series.name != "") {
                        it.name = series.name
                    } else {
                        it.name = context.resources.getString(R.string.unknown)
                    }

                    if (series.firstAirDate != null && series.firstAirDate != "null" && series.firstAirDate != "") {
                        it.firstAirDate = series.firstAirDate.split("-")[0]
                    } else {
                        it.firstAirDate = context.resources.getString(R.string.unknown)
                    }

                    if (series.voteAverage != null) {
                        it.voteAverage = series.voteAverage.toString()
                    }

                    if (series.posterPath != null && series.posterPath != "null" && series.posterPath != "") {
                        loadImage(
                            context,
                            "${BASE_URL_POSTER}${series.posterPath}",
                            it.recyclerviewSeriesItemPosterIV
                        )
                    } else {
                        loadImage(
                            context,
                            null,
                            it.recyclerviewSeriesItemPosterIV
                        )
                    }

                    it.executePendingBindings()

                }

                itemView.setOnClickListener {
                    itemClickListener.onItemClick(series)
                }

            }

        }

    }

    interface ItemClickListener {
        fun onItemClick(series: Series)
    }

}
