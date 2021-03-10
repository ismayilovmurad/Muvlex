package com.martiandeveloper.muvlex.view.feed

import android.content.Intent
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
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentMovieListBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.utils.showToast
import com.martiandeveloper.muvlex.viewmodel.feed.MovieListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MovieListFragment : Fragment(), MovieListAdapter.ItemClickListener {

    private lateinit var viewModel: MovieListViewModel

    private lateinit var binding: FragmentMovieListBinding

    private lateinit var adapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(MovieListViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setRecyclerView()

        viewModel.isProgressLLGone(true)

        return binding.root

    }

    private fun observe() {

        searchResult.observe(viewLifecycleOwner, {

            if (networkAvailable) {

                if (!it.isNullOrEmpty()) {

                    with(viewModel) {
                        setProgressMTVText("${getString(R.string.searching_for)} \"${searchResult.value}\" ...")
                        isProgressLLGone(false)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        viewModel.getData(it, adapter)
                    }

                }

            } else R.string.no_internet_connection.showToast(requireContext())

        })

    }

    private fun setRecyclerView() {

        adapter = MovieListAdapter(this)

        binding.fragmentMovieListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        CoroutineScope(Dispatchers.Main).launch {

            adapter.loadStateFlow.collectLatest {

                with(viewModel) {

                    if (it.refresh is LoadState.NotLoading) if (adapter.itemCount > 0) viewModel.isSearchResultWillAppearHereMTVGone(
                        true
                    )

                    when (it.append) {

                        is LoadState.Loading -> {
                            setProgressMTVText("${getString(R.string.searching_for)} \"${searchResult.value}\" ...")
                            isProgressLLGone(false)
                        }

                        is LoadState.NotLoading -> {
                            setProgressMTVText("")
                            isProgressLLGone(true)
                        }

                        is LoadState.Error -> {
                            setProgressMTVText("")
                            isProgressLLGone(true)
                            if (!networkAvailable) R.string.no_internet_connection.showToast(
                                requireContext()
                            )
                        }

                    }

                }

            }

        }

    }

    override fun onItemClick(movie: Movie) {
        startActivity(Intent(context, MovieDetailActivity::class.java).putExtra("movie", movie))
    }

}
