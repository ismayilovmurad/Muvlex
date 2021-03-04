package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.DATE_PATTERN
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.utils.errorMessageVoid
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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


    //########## Post ProgressBar gone
    private var _postPBGone = MutableLiveData<Boolean>()
    val postPBGone: LiveData<Boolean>
        get() = _postPBGone


    //########## Post ProgressBar gone
    private var _noteCVGone = MutableLiveData<Boolean>()
    val noteCVGone: LiveData<Boolean>
        get() = _noteCVGone


    //########## Got it MaterialTextView click
    fun onGotItMTVClick() {
        _noteCVGone.value = true
    }


    //########## Learn more MaterialTextView click
    private var _learnMoreMTVClick = MutableLiveData<Event<Boolean>>()
    val learnMoreMTVClick: LiveData<Event<Boolean>>
        get() = _learnMoreMTVClick

    fun onLearnMoreMTVClick() {
        _learnMoreMTVClick.value = Event(true)
    }


    //########## RatingBar rating value
    private var _rating = MutableLiveData<Float>()
    val rating: LiveData<Float>
        get() = _rating

    fun setRating(star: Float) {
        _rating.value = star
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
    fun save(movie: Movie) {

        _postMTVGone.value = true
        _postPBGone.value = false

        with(Firebase.auth.currentUser) {

            if (this != null) {

                val map = hashMapOf(
                    "backdropPath" to if (movie.backdropPath.check()) movie.backdropPath else "null",
                    "genreIds" to (movie.genreIds ?: arrayListOf()),
                    "id" to (movie.id ?: 0),
                    "originalLanguage" to if (movie.originalLanguage.check()) movie.originalLanguage else "null",
                    "originalTitle" to if (movie.originalTitle.check()) movie.originalTitle else "null",
                    "overview" to if (movie.overview.check()) movie.overview else "null",
                    "popularity" to (movie.popularity ?: 0.0),
                    "posterPath" to if (movie.posterPath.check()) movie.posterPath else "null",
                    "rating" to rating.value.toString(),
                    "releaseDate" to if (movie.releaseDate.check()) movie.releaseDate else "null",
                    "review" to if (!reviewETText.value.isNullOrEmpty()) {
                        if (reviewETText.value.toString().trimStart().trimEnd()
                                .isNotEmpty()
                        ) reviewETText.value.toString().trimStart().trimEnd() else "No Review"
                    } else "No Review",
                    "time" to (getDateFromString(
                        SimpleDateFormat(
                            DATE_PATTERN,
                            Locale.getDefault()
                        ).format(Date())
                    ) ?: "null"),
                    "title" to if (movie.title.check()) movie.title else "null",
                    "type" to "movie",
                    "uid" to uid,
                    "video" to (movie.video ?: false),
                    "voteAverage" to (movie.voteAverage ?: 0.0),
                    "voteCount" to (movie.voteCount ?: 0)
                )

                Firebase.firestore.collection("posts").document("${uid}_${movie.id}")
                    .set(map).addOnCompleteListener {
                        _postPBGone.value = true
                        _postMTVGone.value = false

                        if (it.isSuccessful) _saveSuccessful.value = true else _errorMessage.value =
                            errorMessageVoid(it)
                    }

            } else _errorMessage.value = Event("")

        }

    }

    private fun getDateFromString(currentDate: String): Date? {
        return try {
            SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).parse(currentDate)
        } catch (e: ParseException) {
            null
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


    init {
        _postPBGone.value = true
    }

}
