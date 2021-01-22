package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class SearchViewModel : ViewModel() {

    //########## Search EditText text
    val searchETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Back ImageView click
    private var _backIVClick = MutableLiveData<Event<Boolean>>()
    val backIVClick: LiveData<Event<Boolean>>
        get() = _backIVClick

    fun onBackIVClick() {
        _backIVClick.value = Event(true)
    }

}
