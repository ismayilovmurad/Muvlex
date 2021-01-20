package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
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


    //########## Is movie list gone
    private var _isMovieListGone = MutableLiveData<Boolean>()
    val isMovieListGone: LiveData<Boolean>
        get() = _isMovieListGone

    fun setMovieListGone(gone: Boolean) {
        _isMovieListGone.value = gone
    }


    //########## Is no internet TextView gone
    private var _isNoInternetTVGone = MutableLiveData<Boolean>()
    val isNoInternetTVGone: LiveData<Boolean>
        get() = _isNoInternetTVGone

    fun setNoInternetTVGone(gone: Boolean) {
        _isNoInternetTVGone.value = gone
    }


    //########## Searching for text
    private var _searchingForText = MutableLiveData<String>()
    val searchingForText: LiveData<String>
        get() = _searchingForText

    fun setSearchingForText(text: String) {
        _searchingForText.value = text
    }


    //########## Is searching for layout gone
    private var _isSearchingForLayoutGone = MutableLiveData<Boolean>()
    val isSearchingForLayoutGone: LiveData<Boolean>
        get() = _isSearchingForLayoutGone

    fun setSearchingForLayoutGone(gone: Boolean) {
        _isSearchingForLayoutGone.value = gone
    }

    //########## Is searching for layout gone 2
    private var _isSearchingForLayoutGone2 = MutableLiveData<Boolean>()
    val isSearchingForLayoutGone2: LiveData<Boolean>
        get() = _isSearchingForLayoutGone2

    fun setSearchingForLayoutGone2(gone: Boolean) {
        _isSearchingForLayoutGone2.value = gone
    }


    //########## No Results Found text
    private var _noResultsFoundText = MutableLiveData<String>()
    val noResultsFoundText: LiveData<String>
        get() = _noResultsFoundText

    fun setNoResultsFoundText(text: String) {
        _noResultsFoundText.value = text
    }


    //########## Is no Results Found layout gone
    private var _isNoResultsFoundTVGone = MutableLiveData<Boolean>()
    val isNoResultsFoundTVGone: LiveData<Boolean>
        get() = _isNoResultsFoundTVGone

    fun setNoResultsFoundGone(gone: Boolean) {
        _isNoResultsFoundTVGone.value = gone
    }

}
