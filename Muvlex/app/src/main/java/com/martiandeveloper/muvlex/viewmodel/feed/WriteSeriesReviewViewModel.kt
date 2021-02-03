package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.errorMessageVoid

class WriteSeriesReviewViewModel : ViewModel() {

    //########## Title MaterialTextView text
    private var _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    fun setTitle(name: String) {
        _name.value = name
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

    private fun isPostMTVGone(gone: Boolean) {
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


    //########## Save rating and review
    fun save(id: String) {

        isPostMTVGone(true)
        isPostPBGone(false)

        val user = Firebase.auth.currentUser

        if (user != null) {

            val usernameMap = hashMapOf(
                "series_id" to id,
                "rating" to _star.value.toString(),
                "review" to if (!reviewETText.value.isNullOrEmpty()) {
                    if (reviewETText.value.toString().trimStart().trimEnd()
                            .isNotEmpty()
                    ) reviewETText.value.toString().trimStart().trimEnd() else "no_review"
                } else "no_review",
                "time" to (System.currentTimeMillis() / 1000).toString()
            )

            Firebase.firestore.collection("series_posts").document("${user.uid}_$id")
                .set(usernameMap).addOnCompleteListener {
                    isPostPBGone(true)
                    isPostMTVGone(false)

                    _saveSuccessful.value = it.isSuccessful

                    if (!_saveSuccessful.value!!) _errorMessage.value = errorMessageVoid(it)
                }

        } else {
            _saveSuccessful.value = false
            _errorMessage.value = Event("")
        }

    }


    //########## Save successful
    private var _saveSuccessful = MutableLiveData<Boolean>()
    val saveSuccessful: LiveData<Boolean>
        get() = _saveSuccessful

    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage

}
