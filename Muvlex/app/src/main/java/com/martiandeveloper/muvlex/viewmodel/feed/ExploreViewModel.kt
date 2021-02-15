package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.martiandeveloper.muvlex.adapter.ExplorePostAdapter
import com.martiandeveloper.muvlex.adapter.ProfilePostAdapter
import com.martiandeveloper.muvlex.repository.ExplorePostSource
import com.martiandeveloper.muvlex.repository.ProfilePostSource
import com.martiandeveloper.muvlex.utils.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ExploreViewModel : ViewModel() {

    //########## Search EditText click
    private var _searchETClick = MutableLiveData<Event<Boolean>>()
    val searchETClick: LiveData<Event<Boolean>>
        get() = _searchETClick

    fun onSearchETClick() {
        _searchETClick.value = Event(true)
    }


    //########## Get data
    fun getData(explorePostAdapter: ExplorePostAdapter) {

        val listData = Pager(PagingConfig(pageSize = 10)) {
            ExplorePostSource(FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                explorePostAdapter.submitData(it)
            }

            explorePostAdapter.loadStateFlow.collectLatest {

                if (it.append is LoadState.Loading) {
                    Timber.d("Loading")
                }

            }

        }

    }

}
