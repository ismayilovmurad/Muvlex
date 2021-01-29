package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentMovieListBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.networkAvailable
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

                        isNoResultsFoundForMTVGone(true)

                        if (networkAvailable) {
                            isNoInternetConnectionMTVGone(true)
                            isMovieRVGone(false)
                            setSearchingForMTVText("${getString(R.string.searching_for)} \"$it\"...")
                            isSearchingForLLGone(false)
                            getData(it, movieListAdapter)
                        } else {
                            isNoInternetConnectionMTVGone(false)
                            isMovieRVGone(true)
                            isSearchingForLLGone(true)
                        }

                    } else {
                        isMovieRVGone(true)
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

                        if (!networkAvailable) {
                            Toast.makeText(
                                context,
                                getString(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

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

                        if (!networkAvailable) {
                            Toast.makeText(
                                context,
                                getString(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

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

        var genreIds = ""
        var id = 0
        var originalLanguage = getString(R.string.unknown)
        var originalTitle = getString(R.string.unknown)
        var overview = getString(R.string.unknown)
        var posterPath = getString(R.string.unknown)
        var releaseDate = getString(R.string.unknown)
        var title = getString(R.string.unknown)
        var voteAverage = getString(R.string.unknown)

        if (movie.genreIds != null && movie.genreIds.isNotEmpty()) {

            for (i in movie.genreIds.indices) {

                when {

                    movie.genreIds[i] == 28 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Action"
                        } else {
                            "Action, "
                        }

                    }

                    movie.genreIds[i] == 12 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Adventure"
                        } else {
                            "Adventure, "
                        }

                    }

                    movie.genreIds[i] == 16 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Animation"
                        } else {
                            "Animation, "
                        }

                    }

                    movie.genreIds[i] == 35 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Comedy"
                        } else {
                            "Comedy, "
                        }

                    }

                    movie.genreIds[i] == 80 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Crime"
                        } else {
                            "Crime, "
                        }

                    }

                    movie.genreIds[i] == 99 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Documentary"
                        } else {
                            "Documentary, "
                        }

                    }

                    movie.genreIds[i] == 18 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Drama"
                        } else {
                            "Drama, "
                        }

                    }

                    movie.genreIds[i] == 10751 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Family"
                        } else {
                            "Family, "
                        }

                    }

                    movie.genreIds[i] == 14 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Fantasy"
                        } else {
                            "Fantasy, "
                        }

                    }

                    movie.genreIds[i] == 36 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "History"
                        } else {
                            "History, "
                        }

                    }

                    movie.genreIds[i] == 27 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Horror"
                        } else {
                            "Horror, "
                        }

                    }

                    movie.genreIds[i] == 10402 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Music"
                        } else {
                            "Music, "
                        }

                    }

                    movie.genreIds[i] == 9648 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Mystery"
                        } else {
                            "Mystery, "
                        }

                    }

                    movie.genreIds[i] == 10749 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Romance"
                        } else {
                            "Romance, "
                        }

                    }

                    movie.genreIds[i] == 878 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Science Fiction"
                        } else {
                            "Science Fiction, "
                        }

                    }

                    movie.genreIds[i] == 10770 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "TV Movie"
                        } else {
                            "TV Movie, "
                        }

                    }

                    movie.genreIds[i] == 53 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Thriller"
                        } else {
                            "Thriller, "
                        }

                    }

                    movie.genreIds[i] == 10752 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "War"
                        } else {
                            "War, "
                        }

                    }

                    movie.genreIds[i] == 37 -> {

                        genreIds += if (movie.genreIds.lastIndex == i) {
                            "Western"
                        } else {
                            "Western, "
                        }

                    }

                }

            }

        }

        if (movie.id != null) {
            id = movie.id
        }

        if (movie.originalLanguage != null && movie.originalLanguage != "null" && movie.originalLanguage != "") {
            originalLanguage = movie.originalLanguage
        }

        if (movie.originalTitle != null && movie.originalTitle != "null" && movie.originalTitle != "") {
            originalTitle = movie.originalTitle
        }

        if (movie.overview != null && movie.overview != "null" && movie.overview != "") {
            overview = movie.overview
        }

        if (movie.posterPath != null && movie.posterPath != "null" && movie.posterPath != "") {
            posterPath = movie.posterPath
        }

        if (movie.releaseDate != null && movie.releaseDate != "null" && movie.releaseDate != "") {
            releaseDate = movie.releaseDate
        }

        if (movie.title != null && movie.title != "null" && movie.title != "") {
            title = movie.title
        }

        if (movie.voteAverage != null) {
            voteAverage = movie.voteAverage.toString()
        }

        navigate(
            SearchFragmentDirections.actionSearchFragmentToRateMovieFragment(
                genreIds,
                id,
                originalLanguage,
                originalTitle,
                overview,
                posterPath,
                releaseDate,
                title,
                voteAverage
            )
        )

    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

}
