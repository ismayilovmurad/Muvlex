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
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.repository.UserDataSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class UserListViewModel:ViewModel() {

    //########## Movie RecyclerView gone
    private var _movieRVGone = MutableLiveData<Boolean>()
    val movieRVGone: LiveData<Boolean>
        get() = _movieRVGone

    fun isMovieRVGone(gone: Boolean) {
        _movieRVGone.value = gone
    }


    //########## No internet connection MaterialTextView gone
    private var _noInternetConnectionMTVGone = MutableLiveData<Boolean>()
    val noInternetConnectionMTVGone: LiveData<Boolean>
        get() = _noInternetConnectionMTVGone

    fun isNoInternetConnectionMTVGone(gone: Boolean) {
        _noInternetConnectionMTVGone.value = gone
    }


    //########## Searching for MaterialTextView text
    private var _searchingForMTVText = MutableLiveData<String>()
    val searchingForMTVText: LiveData<String>
        get() = _searchingForMTVText

    fun setSearchingForMTVText(text: String) {
        _searchingForMTVText.value = text
    }


    //########## Searching for LinearLayout gone
    private var _searchingForLLGone = MutableLiveData<Boolean>()
    val searchingForLLGone: LiveData<Boolean>
        get() = _searchingForLLGone

    fun isSearchingForLLGone(gone: Boolean) {
        _searchingForLLGone.value = gone
    }


    //########## Searching for LinearLayout 2 gone
    private var _searchingForLL2Gone = MutableLiveData<Boolean>()
    val searchingForLL2Gone: LiveData<Boolean>
        get() = _searchingForLL2Gone

    fun isSearchingForLL2Gone(gone: Boolean) {
        _searchingForLL2Gone.value = gone
    }


    //########## No Results Found for MaterialTextView text
    private var _noResultsFoundForMTVText = MutableLiveData<String>()
    val noResultsFoundForMTVText: LiveData<String>
        get() = _noResultsFoundForMTVText

    fun setNoResultsFoundForMTVText(text: String) {
        _noResultsFoundForMTVText.value = text
    }


    //########## No Results Found for MaterialTextView gone
    private var _noResultsFoundForMTVGone = MutableLiveData<Boolean>()
    val noResultsFoundForMTVGone: LiveData<Boolean>
        get() = _noResultsFoundForMTVGone

    fun isNoResultsFoundForMTVGone(gone: Boolean) {
        _noResultsFoundForMTVGone.value = gone
    }

    //########## Get data
    fun getData(userListAdapter: UserPostAdapter) {

        val listData = Pager(PagingConfig(pageSize = 10)) {
            UserDataSource(FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                userListAdapter.submitData(it)
            }

            userListAdapter.loadStateFlow.collectLatest {

                if (it.append is LoadState.Loading) {
                    Timber.d("Loading")
                }

            }

        }

    }
}