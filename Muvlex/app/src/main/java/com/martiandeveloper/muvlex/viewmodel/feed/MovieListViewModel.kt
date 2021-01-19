package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.martiandeveloper.muvlex.adapter.MovieListAdapter
import com.martiandeveloper.muvlex.repository.MovieDataSource
import com.martiandeveloper.muvlex.service.MovieApi
import com.martiandeveloper.muvlex.service.MovieService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MovieListViewModel : ViewModel() {

    //########## Movie API
    private val movieApi: MutableLiveData<MovieApi> by lazy { MutableLiveData<MovieApi>() }

    init {
        movieApi.value = MovieService.getClient()
    }

    //########## Get data
    fun getData(movieTitle: String, movieListAdapter: MovieListAdapter) {

        val listData = Pager(PagingConfig(pageSize = 20)) {
            MovieDataSource(movieTitle, movieApi.value!!)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                movieListAdapter.submitData(it)
            }

        }

    }

}
