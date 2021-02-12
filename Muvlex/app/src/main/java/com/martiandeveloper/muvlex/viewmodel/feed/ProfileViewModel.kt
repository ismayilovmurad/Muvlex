package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.martiandeveloper.muvlex.adapter.ProfilePostAdapter
import com.martiandeveloper.muvlex.repository.ProfilePostSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel : ViewModel() {

    //########## Get data
    fun getData(profilePostAdapter: ProfilePostAdapter) {

        val listData = Pager(PagingConfig(pageSize = 10)) {
            ProfilePostSource(FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                profilePostAdapter.submitData(it)
            }

            profilePostAdapter.loadStateFlow.collectLatest {

                if (it.append is LoadState.Loading) {
                    Timber.d("Loading")
                }

            }

        }

    }

}
