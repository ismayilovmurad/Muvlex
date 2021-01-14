package com.martiandeveloper.muvlex.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.model.Language
import com.martiandeveloper.muvlex.utils.Event

class MainViewModel : ViewModel() {

    //########## On layout language click
    private var _onLayoutLanguageClick = MutableLiveData<Event<Boolean>>()
    val onLayoutLanguageClick: LiveData<Event<Boolean>>
        get() = _onLayoutLanguageClick

    fun onLayoutLanguageClick() {
        _onLayoutLanguageClick.value = Event(true)
    }


    //########## Is layout language gone
    private var _isLayoutLanguageGone = MutableLiveData<Boolean>()
    val isLayoutLanguageGone: LiveData<Boolean>
        get() = _isLayoutLanguageGone

    fun setLayoutLanguageGone(gone: Boolean) {
        _isLayoutLanguageGone.value = gone
    }


    //########## Search EditText content
    val searchETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Language list
    private var _languageList = MutableLiveData<ArrayList<Language>>()
    val languageList: LiveData<ArrayList<Language>>
        get() = _languageList

    fun fillTheLanguageList(list: ArrayList<Language>) {
        _languageList.value = list
    }


    init {
        _languageList.value = ArrayList()
    }

}
