package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.martiandeveloper.muvlex.adapter.SeriesListAdapter
import com.martiandeveloper.muvlex.repository.SeriesDataSource
import com.martiandeveloper.muvlex.service.TmdbApi
import com.martiandeveloper.muvlex.service.TmdbService
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SeriesListViewModel : ViewModel() {

    //########## TMDB API
    private val api: MutableLiveData<TmdbApi> by lazy { MutableLiveData<TmdbApi>() }


    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Progress LinearLayout gone
    private var _progressLLGone = MutableLiveData<Boolean>()
    val progressLLGone: LiveData<Boolean>
        get() = _progressLLGone

    fun isProgressLLGone(gone: Boolean) {
        _progressLLGone.value = gone
    }


    //########## Search result will appear here MaterialTextView gone
    private var _searchResultWillAppearHereMTVGone = MutableLiveData<Boolean>()
    val searchResultWillAppearHereMTVGone: LiveData<Boolean>
        get() = _searchResultWillAppearHereMTVGone

    fun isSearchResultWillAppearHereMTVGone(gone: Boolean) {
        _searchResultWillAppearHereMTVGone.value = gone
    }


    //########## Get data
    fun getData(query: String, adapter: SeriesListAdapter) {

        val listData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            SeriesDataSource(query, api.value!!)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                adapter.submitData(it)
            }

        }

    }


    init {

        viewModelScope.launch {
            api.value = TmdbService.getClient()
        }

    }

}
