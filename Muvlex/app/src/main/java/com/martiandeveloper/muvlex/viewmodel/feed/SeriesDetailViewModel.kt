package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class SeriesDetailViewModel : ViewModel() {

    //########## Main MaterialToolbar title
    private var _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    fun setName(name: String) {
        _name.value = name
    }


    //########## Main MaterialToolbar gone
    private var _mainMTBGone = MutableLiveData<Boolean>()
    val mainMTBGone: LiveData<Boolean>
        get() = _mainMTBGone

    fun isMainMTBGone(gone: Boolean) {
        _mainMTBGone.value = gone
    }


    //########## Keep MaterialTextView click
    private var _keepMTVClick = MutableLiveData<Event<Boolean>>()
    val keepMTVClick: LiveData<Event<Boolean>>
        get() = _keepMTVClick

    fun onKeepMTVClick() {
        _keepMTVClick.value = Event(true)
    }


    //########## Discard MaterialTextView click
    private var _discardMTVClick = MutableLiveData<Event<Boolean>>()
    val discardMTVClick: LiveData<Event<Boolean>>
        get() = _discardMTVClick

    fun onDiscardMTVClick() {
        _discardMTVClick.value = Event(true)
    }

}
