package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class ExploreViewModel : ViewModel() {

    //########## Search EditText click
    private var _searchETClick = MutableLiveData<Event<Boolean>>()
    val searchETClick: LiveData<Event<Boolean>>
        get() = _searchETClick

    fun onSearchETClick() {
        _searchETClick.value = Event(true)
    }

}
