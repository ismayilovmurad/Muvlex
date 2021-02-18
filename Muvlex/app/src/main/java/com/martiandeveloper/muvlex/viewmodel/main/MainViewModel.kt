package com.martiandeveloper.muvlex.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.model.Language
import com.martiandeveloper.muvlex.utils.Event

class MainViewModel : ViewModel() {

    //########## Language LinearLayout click
    private var _languageLLClick = MutableLiveData<Event<Boolean>>()
    val languageLLClick: LiveData<Event<Boolean>>
        get() = _languageLLClick

    fun onLanguageLLClick() {
        _languageLLClick.value = Event(true)
    }


    //########## Language LinearLayout gone
    private var _languageLLGone = MutableLiveData<Boolean>()
    val languageLLGone: LiveData<Boolean>
        get() = _languageLLGone

    fun isLanguageLLGone(gone: Boolean) {
        _languageLLGone.value = gone
    }


    //########## Search EditText text
    val searchETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Language list
    private var _languageList = MutableLiveData<ArrayList<Language>>()
    val languageList: LiveData<ArrayList<Language>>
        get() = _languageList

    fun fillLanguageList(list: ArrayList<Language>) {
        _languageList.value = list
    }


    init {
        _languageList.value = ArrayList()
        _languageLLGone.value = true
    }

}
