package com.martiandeveloper.muvlex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewMovieItemBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.BASE_URL_POSTER
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.utils.load

class MovieListAdapter(
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return MovieListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_movie_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieListViewHolder).bind(context, getItem(position), itemCLickListener)
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    class MovieListViewHolder(private val binding: RecyclerviewMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            movie: Movie?,
            itemClickListener: ItemClickListener
        ) {
            if (movie != null)

                binding.let {

                    with(movie) {

                        it.title =
                            if (title.check()) title else context.resources.getString(
                                R.string.unknown
                            )

                        it.releaseDate =
                            if (releaseDate.check()) releaseDate!!.split(
                                "-"
                            )[0] else context.resources.getString(R.string.unknown)

                        it.voteAverage =
                            voteAverage?.toString() ?: context.resources.getString(R.string.unknown)

                        it.recyclerviewMovieItemPosterIV.load(
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
        fun onItemClick(movie: Movie)
    }

}
