package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentMovieListBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.MovieListViewModel
import kotlinx.coroutines.launch

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
            isNoInternetConnectionMTVGone(true)
            isSearchingForLLGone(true)
            isSearchingForLL2Gone(true)
            isNoResultsFoundForMTVGone(true)
        }

        setRecyclerView()

        return fragmentMovieListBinding.root

    }

    private fun observe() {

        with(movieListViewModel) {

            searchResult.observe(viewLifecycleOwner, {

                viewLifecycleOwner.lifecycleScope.launch {

                    if (!it.isNullOrEmpty()) {

                        isSearchingForLLGone(false)

                        if (isNetworkAvailable) {
                            isNoInternetConnectionMTVGone(true)
                            isMovieRVGone(false)
                            setSearchingForMTVText("${getString(R.string.searching_for)} \"$it\"...")
                            getData(it, movieListAdapter)
                        } else {
                            isNoInternetConnectionMTVGone(false)
                            isMovieRVGone(true)
                        }

                    } else {
                        isMovieRVGone(true)
                        isNoResultsFoundForMTVGone(true)
                    }

                }

            })

        }

    }

    private fun setRecyclerView() {

        var isFirstAppend = true

        movieListAdapter = MovieListAdapter(this)

        with(movieListViewModel) {

            movieListAdapter.addLoadStateListener {

                when (it.append) {

                    is LoadState.Loading -> {

                        if (isFirstAppend) {
                            isSearchingForLLGone(false)
                            isFirstAppend = false
                        } else {
                            isSearchingForLL2Gone(false)
                        }

                    }

                    is LoadState.NotLoading -> {
                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)
                    }

                    is LoadState.Error -> {
                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)
                    }

                }

                when (it.refresh) {

                    is LoadState.Loading -> {
                        isFirstAppend = true
                    }

                    is LoadState.NotLoading -> {

                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)

                        if (movieListAdapter.itemCount == 0) {

                            if (searchResult.value != null) {
                                setNoResultsFoundForMTVText("${getString(R.string.no_results_found_for)} \"${searchResult.value}\"")
                                isNoResultsFoundForMTVGone(false)
                            } else {
                                isNoResultsFoundForMTVGone(true)
                            }

                        } else {
                            isNoResultsFoundForMTVGone(true)
                        }

                    }

                    is LoadState.Error -> {
                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)
                    }

                }

            }

        }

        fragmentMovieListBinding.fragmentMovieListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = movieListAdapter
        }

    }

    override fun onItemClick(movie: Movie) {
        TODO()
    }

}
