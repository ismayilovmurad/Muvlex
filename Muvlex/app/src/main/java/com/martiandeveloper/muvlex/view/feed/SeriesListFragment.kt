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
        TODO()
    }

}
