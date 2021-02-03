package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.SeriesListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentSeriesListBinding
import com.martiandeveloper.muvlex.model.Series
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.utils.showToast
import com.martiandeveloper.muvlex.viewmodel.feed.SeriesListViewModel

class SeriesListFragment : Fragment(), SeriesListAdapter.ItemClickListener {

    private lateinit var seriesListViewModel: SeriesListViewModel

    private lateinit var fragmentSeriesListBinding: FragmentSeriesListBinding

    private lateinit var seriesListAdapter: SeriesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        seriesListViewModel = ViewModelProviders.of(this).get(SeriesListViewModel::class.java)

        fragmentSeriesListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_series_list, container, false)

        fragmentSeriesListBinding.let {
            it.seriesListViewModel = seriesListViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        with(seriesListViewModel) {
            isNoInternetConnectionMTVGone(true)
            isSearchingForLLGone(true)
            isSearchingForLL2Gone(true)
            isNoResultsFoundForMTVGone(true)
        }

        setRecyclerView()

        return fragmentSeriesListBinding.root

    }

    private fun observe() {

        with(seriesListViewModel) {

            searchResult.observe(viewLifecycleOwner, {

                if (!it.isNullOrEmpty()) {

                    isNoResultsFoundForMTVGone(true)

                    if (networkAvailable) {
                        isNoInternetConnectionMTVGone(true)
                        isSeriesRVGone(false)
                        setSearchingForMTVText("${getString(R.string.searching_for)} \"$it\"...")
                        isSearchingForLLGone(false)
                        getData(it, seriesListAdapter)
                    } else {
                        isNoInternetConnectionMTVGone(false)
                        isSeriesRVGone(true)
                        isSearchingForLLGone(true)
                    }

                } else isSeriesRVGone(true)

            })

        }

    }

    private fun setRecyclerView() {

        var isFirstAppend = true

        seriesListAdapter = SeriesListAdapter(this)

        with(seriesListViewModel) {

            seriesListAdapter.addLoadStateListener {

                when (it.append) {

                    is LoadState.Loading -> {

                        if (isFirstAppend) {
                            isSearchingForLLGone(false)
                            isFirstAppend = false
                        } else isSearchingForLL2Gone(false)

                    }

                    is LoadState.NotLoading -> {
                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)
                    }

                    is LoadState.Error -> {
                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)

                        if (!networkAvailable) R.string.no_internet_connection.showToast(
                            requireContext()
                        )
                    }

                }

                when (it.refresh) {

                    is LoadState.Loading -> {
                        isFirstAppend = true
                    }

                    is LoadState.NotLoading -> {

                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)

                        if (seriesListAdapter.itemCount == 0) {

                            if (searchResult.value != null) {
                                setNoResultsFoundForMTVText("${getString(R.string.no_results_found_for)} \"${searchResult.value}\"")
                                isNoResultsFoundForMTVGone(false)
                            } else isNoResultsFoundForMTVGone(true)

                        } else isNoResultsFoundForMTVGone(true)

                    }

                    is LoadState.Error -> {
                        isSearchingForLLGone(true)
                        isSearchingForLL2Gone(true)

                        if (!networkAvailable) R.string.no_internet_connection.showToast(
                            requireContext()
                        )
                    }

                }

            }

        }

        fragmentSeriesListBinding.fragmentSeriesListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = seriesListAdapter
        }

    }

    override fun onItemClick(series: Series) {

        var genreIds = ""
        var id = 0
        var originalLanguage = getString(R.string.unknown)
        var originalName = getString(R.string.unknown)
        var overview = getString(R.string.unknown)
        var posterPath = getString(R.string.unknown)
        var firstAirDate = getString(R.string.unknown)
        var name = getString(R.string.unknown)
        var voteAverage = getString(R.string.unknown)

        if (series.genreIds != null && series.genreIds.isNotEmpty())

            for (i in series.genreIds.indices) {

                with(series.genreIds[i]) {

                    when {
                        this == 10759 -> genreIds += if (series.genreIds.lastIndex == i) "Action & Adventure" else "Action & Adventure, "
                        this == 16 -> genreIds += if (series.genreIds.lastIndex == i) "Animation" else "Animation, "
                        this == 35 -> genreIds += if (series.genreIds.lastIndex == i) "Comedy" else "Comedy, "
                        this == 80 -> genreIds += if (series.genreIds.lastIndex == i) "Crime" else "Crime, "
                        this == 99 -> genreIds += if (series.genreIds.lastIndex == i) "Documentary" else "Documentary, "
                        this == 18 -> genreIds += if (series.genreIds.lastIndex == i) "Drama" else "Drama, "
                        this == 10751 -> genreIds += if (series.genreIds.lastIndex == i) "Family" else "Family, "
                        this == 10762 -> genreIds += if (series.genreIds.lastIndex == i) "Kids" else "Kids, "
                        this == 9648 -> genreIds += if (series.genreIds.lastIndex == i) "Mystery" else "Mystery, "
                        this == 10763 -> genreIds += if (series.genreIds.lastIndex == i) "News" else "News, "
                        this == 10764 -> genreIds += if (series.genreIds.lastIndex == i) "Reality" else "Reality, "
                        this == 10765 -> genreIds += if (series.genreIds.lastIndex == i) "Sci-Fi & Fantasy" else "Sci-Fi & Fantasy, "
                        this == 10766 -> genreIds += if (series.genreIds.lastIndex == i) "Soap" else "Soap, "
                        this == 10767 -> genreIds += if (series.genreIds.lastIndex == i) "Talk" else "Talk, "
                        this == 10768 -> genreIds += if (series.genreIds.lastIndex == i) "War & Politics" else "War & Politics, "
                        this == 37 -> genreIds += if (series.genreIds.lastIndex == i) "Western" else "Western, "
                    }

                }

            }

        if (series.id != null) id = series.id

        if (series.originalLanguage != null && series.originalLanguage != "null" && series.originalLanguage != "") originalLanguage =
            series.originalLanguage

        if (series.originalName != null && series.originalName != "null" && series.originalName != "") originalName =
            series.originalName

        if (series.overview != null && series.overview != "null" && series.overview != "") overview =
            series.overview

        if (series.posterPath != null && series.posterPath != "null" && series.posterPath != "") posterPath =
            series.posterPath

        if (series.firstAirDate != null && series.firstAirDate != "null" && series.firstAirDate != "") firstAirDate =
            series.firstAirDate

        if (series.name != null && series.name != "null" && series.name != "") name = series.name

        if (series.voteAverage != null) voteAverage = series.voteAverage.toString()

        view.navigate(
            SearchFragmentDirections.actionSearchFragmentToRateMovieFragment(
                genreIds,
                id,
                originalLanguage,
                originalName,
                overview,
                posterPath,
                firstAirDate,
                name,
                voteAverage
            )
        )

    }

}
