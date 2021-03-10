package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class UserDetailViewModel : ViewModel() {

    //########## Main MaterialToolbar title
    private var _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String) {
        _title.value = title
    }

}
