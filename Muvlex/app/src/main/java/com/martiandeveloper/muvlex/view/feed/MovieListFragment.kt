package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentMovieListBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.MovieListViewModel

class MovieListFragment : Fragment(), MovieListAdapter.ItemClickListener {

    private lateinit var fragmentMovieListBinding: FragmentMovieListBinding

    private lateinit var movieListViewModel: MovieListViewModel

    private lateinit var movieListAdapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        movieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel::class.java)

        fragmentMovieListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)

        fragmentMovieListBinding.let {
            it.movieListViewModel = movieListViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setRecyclerView()

        return fragmentMovieListBinding.root

    }

    private fun observe() {

        with(movieListViewModel) {

            searchResult.observe(viewLifecycleOwner, {
                getData(it, movieListAdapter)
            })

        }

    }

    private fun setRecyclerView() {

        movieListAdapter = MovieListAdapter(this)

        fragmentMovieListBinding.fragmentMovieListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = movieListAdapter
        }

    }

    override fun onItemClick(movie: Movie) {
        Toast.makeText(context, movie.title, Toast.LENGTH_SHORT).show()
    }

}
