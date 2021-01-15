package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class ExploreViewModel : ViewModel() {

    //########## On search EditTex click
    private var _onSearchEditTextClick = MutableLiveData<Event<Boolean>>()
    val onSearchEditTextClick: LiveData<Event<Boolean>>
        get() = _onSearchEditTextClick

    fun onSearchEditTextClick() {
        _onSearchEditTextClick.value = Event(true)
    }

}
