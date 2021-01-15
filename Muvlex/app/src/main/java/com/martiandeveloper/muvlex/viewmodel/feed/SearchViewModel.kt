package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class SearchViewModel : ViewModel() {

    //########## Search EditText content
    val searchETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## On back ImageView click
    private var _onBackImageViewClick = MutableLiveData<Event<Boolean>>()
    val onBackImageViewClick: LiveData<Event<Boolean>>
        get() = _onBackImageViewClick

    fun onBackImageViewClick() {
        _onBackImageViewClick.value = Event(true)
    }

}
