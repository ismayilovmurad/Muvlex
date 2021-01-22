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
import com.martiandeveloper.muvlex.utils.loadImage

class MovieListAdapter(
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        val binding: RecyclerviewMovieItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.recyclerview_movie_item,
            parent,
            false
        )

        return MovieListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieListViewHolder).bind(getItem(position), context, itemCLickListener)
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
            movie: Movie?,
            context: Context,
            itemClickListener: ItemClickListener
        ) {

            if (movie != null) {

                binding.let {

                    if (movie.title != null && movie.title != "null" && movie.title != "") {
                        it.title = movie.title
                    } else {
                        it.title = context.resources.getString(R.string.unknown)
                    }

                    if (movie.releaseDate != null && movie.releaseDate != "null" && movie.releaseDate != "") {
                        it.releaseDate = movie.releaseDate.split("-")[0]
                    } else {
                        it.releaseDate = context.resources.getString(R.string.unknown)
                    }

                    if (movie.voteAverage != null) {
                        it.voteAverage = movie.voteAverage.toString()
                    }

                    if (movie.posterPath != null && movie.posterPath != "null" && movie.posterPath != "") {
                        loadImage(
                            context,
                            "${BASE_URL_POSTER}${movie.posterPath}",
                            it.recyclerviewMovieItemPosterIV
                        )
                    } else {
                        loadImage(
                            context,
                            null,
                            it.recyclerviewMovieItemPosterIV
                        )
                    }

                    it.executePendingBindings()

                }

                itemView.setOnClickListener {
                    itemClickListener.onItemClick(movie)
                }

            }

        }

    }

    interface ItemClickListener {
        fun onItemClick(movie: Movie)
    }

}
