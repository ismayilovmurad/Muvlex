package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RateSeriesViewModel : ViewModel() {

    //########## First air date MaterialTextView text
    private var _firstAirDate = MutableLiveData<String>()
    val firstAirDate: LiveData<String>
        get() = _firstAirDate

    fun setFirstAirDate(firstAirDate: String) {
        _firstAirDate.value = firstAirDate
    }


    //########## Vote average MaterialTextView text
    private var _voteAverage = MutableLiveData<String>()
    val voteAverage: LiveData<String>
        get() = _voteAverage

    fun setVoteAverage(voteAverage: String) {
        _voteAverage.value = voteAverage
    }


    //########## Genre MaterialTextView text
    private var _genre = MutableLiveData<String>()
    val genre: LiveData<String>
        get() = _genre

    fun setGenre(genre: String) {
        _genre.value = genre
    }


    //########## Language MaterialTextView text
    private var _language = MutableLiveData<String>()
    val language: LiveData<String>
        get() = _language

    fun setLanguage(language: String) {
        _language.value = language
    }


    //########## Overview MaterialTextView text
    private var _overview = MutableLiveData<String>()
    val overview: LiveData<String>
        get() = _overview

    fun setOverview(overview: String) {
        _overview.value = overview
    }

}
