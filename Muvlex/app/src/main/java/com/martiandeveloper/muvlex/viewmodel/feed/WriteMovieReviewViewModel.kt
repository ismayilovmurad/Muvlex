package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class WriteMovieReviewViewModel : ViewModel() {

    //########## Title MaterialTextView text
    private var _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String) {
        _title.value = title
    }


    //########## Post MaterialTextView click
    private var _postMTVClick = MutableLiveData<Event<Boolean>>()
    val postMTVClick: LiveData<Event<Boolean>>
        get() = _postMTVClick

    fun onPostMTVClick() {
        _postMTVClick.value = Event(true)
    }


    //########## Post MaterialTextView gone
    private var _postMTVGone = MutableLiveData<Boolean>()
    val postMTVGone: LiveData<Boolean>
        get() = _postMTVGone

    fun isPostMTVGone(gone: Boolean) {
        _postMTVGone.value = gone
    }


    //########## Post ProgressBar gone
    private var _postPBGone = MutableLiveData<Boolean>()
    val postPBGone: LiveData<Boolean>
        get() = _postPBGone

    fun isPostPBGone(gone: Boolean) {
        _postPBGone.value = gone
    }


    //########## Post ProgressBar gone
    private var _noteCVGone = MutableLiveData<Boolean>()
    val noteCVGone: LiveData<Boolean>
        get() = _noteCVGone

    fun isNoteCVGone(gone: Boolean) {
        _noteCVGone.value = gone
    }


    //########## Got it MaterialTextView click
    private var _gotItMTVClick = MutableLiveData<Event<Boolean>>()
    val gotItMTVClick: LiveData<Event<Boolean>>
        get() = _gotItMTVClick

    fun onGotItMTVClick() {
        _gotItMTVClick.value = Event(true)
    }


    //########## Learn more MaterialTextView click
    private var _learnMoreMTVClick = MutableLiveData<Event<Boolean>>()
    val learnMoreMTVClick: LiveData<Event<Boolean>>
        get() = _learnMoreMTVClick

    fun onLearnMoreMTVClick() {
        _learnMoreMTVClick.value = Event(true)
    }


    //########## RatingBar star
    private var _star = MutableLiveData<Float>()
    val star: LiveData<Float>
        get() = _star

    fun setStar(star: Float) {
        _star.value = star
    }


    //########## Review EditText text
    val reviewETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }

}
