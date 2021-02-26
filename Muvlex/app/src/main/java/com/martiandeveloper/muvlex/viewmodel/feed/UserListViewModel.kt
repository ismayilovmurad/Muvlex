package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.martiandeveloper.muvlex.adapter.UserListAdapter
import com.martiandeveloper.muvlex.repository.UserDataSource
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserListViewModel : ViewModel() {

    //########## Get data
    fun getData(query: String, adapter: UserListAdapter) {

        val listData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            UserDataSource(query, FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                adapter.submitData(it)
            }

        }

    }

}
