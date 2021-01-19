package com.martiandeveloper.muvlex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewMovieItemBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.service.POSTER_BASE_URL

class MovieListAdapter(
    private val context: Context,
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: RecyclerviewMovieItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
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

                val title = movie.title + " (" + movie.releaseDate?.split("-")?.get(0) + ")"

                binding.let {
                    it.title = title

                    it.voteAverage = movie.voteAverage.toString()

                    Glide.with(context)
                        .load("${POSTER_BASE_URL}${movie.posterPath}")
                        .placeholder(R.drawable.muvlex_original_logo)
                        .centerCrop()
                        .into(it.recyclerviewMovieItemPosterIV)

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
