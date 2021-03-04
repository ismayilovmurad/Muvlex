package com.martiandeveloper.muvlex.view.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.SeriesListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentSeriesListBinding
import com.martiandeveloper.muvlex.model.Series
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.SeriesListViewModel

class SeriesListFragment : Fragment(), SeriesListAdapter.ItemClickListener {

    private lateinit var viewModel: SeriesListViewModel

    private lateinit var binding: FragmentSeriesListBinding

    private lateinit var adapter: SeriesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this).get(SeriesListViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_series_list, container, false)

        observe()

        setRecyclerView()

        return binding.root
    }

    private fun observe() {

        searchResult.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) viewModel.getData(it, adapter)
        })

    }

    private fun setRecyclerView() {

        adapter = SeriesListAdapter(this)

        binding.fragmentSeriesListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

    }

    override fun onItemClick(series: Series) {
        startActivity(Intent(context, SeriesDetailActivity::class.java).putExtra("series", series))
    }

}
