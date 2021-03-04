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
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentMovieListBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.MovieListViewModel

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

        adapter = MovieListAdapter(this)

        binding.fragmentMovieListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

    }

    override fun onItemClick(movie: Movie) {
        startActivity(Intent(context, MovieDetailActivity::class.java).putExtra("movie", movie))
    }

}
