package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentMovieListBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.MovieListViewModel
import timber.log.Timber

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

        with(movieListViewModel) {
            setNoInternetTVGone(true)
            setSearchingForLayoutGone(true)
            setSearchingForLayoutGone2(true)
            setNoResultsFoundGone(true)
        }

        setRecyclerView()

        return fragmentMovieListBinding.root

    }

    private fun observe() {

        with(movieListViewModel) {

            searchResult.observe(viewLifecycleOwner, {

                if (!it.isNullOrEmpty()) {

                    if (isNetworkAvailable) {
                        setNoInternetTVGone(true)
                        setMovieListGone(false)
                        setSearchingForText("${getString(R.string.searching_for)} \"$it\"...")
                        getData(it, movieListAdapter)
                    } else {
                        setNoInternetTVGone(false)
                        setMovieListGone(true)
                    }

                } else {
                    setMovieListGone(true)
                    setNoResultsFoundGone(true)
                }

            })

        }

    }

    private fun setRecyclerView() {

        var isFirstAppend = true

        movieListAdapter = MovieListAdapter(this)

        movieListAdapter.addLoadStateListener {

            when (it.append) {

                is LoadState.Loading -> {

                    if (isFirstAppend) {
                        Timber.d("Show top refresh")
                        movieListViewModel.setSearchingForLayoutGone(false)
                        isFirstAppend = false
                    } else {
                        Timber.d("Show bottom refresh")
                        movieListViewModel.setSearchingForLayoutGone2(false)
                    }

                }

                is LoadState.NotLoading -> {
                    Timber.e("Hide top refresh")
                    Timber.e("Hide bottom refresh")
                    movieListViewModel.setSearchingForLayoutGone(true)
                    movieListViewModel.setSearchingForLayoutGone2(true)
                }

                is LoadState.Error -> {
                    Timber.e("Hide top refresh")
                    Timber.e("Hide bottom refresh")
                    movieListViewModel.setSearchingForLayoutGone(true)
                    movieListViewModel.setSearchingForLayoutGone2(true)
                }

            }

            when (it.refresh) {

                is LoadState.Loading -> {
                    isFirstAppend = true
                }

                is LoadState.NotLoading -> {

                    Timber.e("Hide top refresh")
                    Timber.e("Hide bottom refresh")
                    movieListViewModel.setSearchingForLayoutGone(true)
                    movieListViewModel.setSearchingForLayoutGone2(true)

                    if (movieListAdapter.itemCount == 0) {

                        if (searchResult.value != null) {
                            movieListViewModel.setNoResultsFoundText("${getString(R.string.no_results_found_for)} \"${searchResult.value}\"")
                            movieListViewModel.setNoResultsFoundGone(false)
                        } else {
                            movieListViewModel.setNoResultsFoundGone(true)
                        }

                    } else {
                        movieListViewModel.setNoResultsFoundGone(true)
                    }

                }

                is LoadState.Error -> {
                    Timber.e("Hide top refresh")
                    Timber.e("Hide bottom refresh")
                    movieListViewModel.setSearchingForLayoutGone(true)
                    movieListViewModel.setSearchingForLayoutGone2(true)
                }

            }

        }

        fragmentMovieListBinding.fragmentMovieListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = movieListAdapter
        }

    }

    override fun onItemClick(movie: Movie) {
        Toast.makeText(context, movie.title, Toast.LENGTH_SHORT).show()
    }

}
