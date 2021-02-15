package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.martiandeveloper.muvlex.adapter.HomePostAdapter
import com.martiandeveloper.muvlex.repository.HomePostSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel:ViewModel() {

    //########## Get data
    fun getData(homePostAdapter: HomePostAdapter,friendList:ArrayList<String>) {

        val listData = Pager(PagingConfig(pageSize = 10)) {
            HomePostSource(FirebaseFirestore.getInstance(),friendList)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                homePostAdapter.submitData(it)
            }

            homePostAdapter.loadStateFlow.collectLatest {

                if (it.append is LoadState.Loading) {
                    Timber.d("Loading")
                }

            }

        }

    }
}