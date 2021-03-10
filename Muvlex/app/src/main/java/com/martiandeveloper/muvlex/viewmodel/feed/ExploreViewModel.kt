package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.martiandeveloper.muvlex.adapter.PostAdapter
import com.martiandeveloper.muvlex.repository.ExplorePostDataSource
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {

    //########## Search EditText click
    private var _searchETClick = MutableLiveData<Event<Boolean>>()
    val searchETClick: LiveData<Event<Boolean>>
        get() = _searchETClick

    fun onSearchETClick() {
        _searchETClick.value = Event(true)
    }


    //########## Posts will appear here LinearLayout gone
    private var _postsWillAppearHereLLGone = MutableLiveData<Boolean>()
    val postsWillAppearHereLLGone: LiveData<Boolean>
        get() = _postsWillAppearHereLLGone

    fun isPostsWillAppearHereLLGone(gone: Boolean) {
        _postsWillAppearHereLLGone.value = gone
    }


    //########## Get data
    fun getData(adapter: PostAdapter) {

        val listData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            ExplorePostDataSource(FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                adapter.submitData(it)
            }

        }

    }

}
