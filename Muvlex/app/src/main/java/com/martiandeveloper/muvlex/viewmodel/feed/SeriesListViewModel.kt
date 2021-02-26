package com.martiandeveloper.muvlex.viewmodel.feed

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

    //########## Series API
    private val api: MutableLiveData<TmdbApi> by lazy { MutableLiveData<TmdbApi>() }


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
        api.value = TmdbService.getClient()
    }

}
