package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.*

class SignUpViewModel : ViewModel() {

    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Email EditText text
    val emailETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Password EditText text
    val passwordETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Confirm password EditText text
    val confirmPasswordETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Next MaterialButton click
    private var _nextMBTNClick = MutableLiveData<Event<Boolean>>()
    val nextMBTNClick: LiveData<Event<Boolean>>
        get() = _nextMBTNClick

    fun onNextMBTNClick() {
        _nextMBTNClick.value = Event(true)
    }


    //########## Next MaterialButton enable
    private var _nextMBTNEnable = MutableLiveData<Boolean>()
    val nextMBTNEnable: LiveData<Boolean>
        get() = _nextMBTNEnable

    fun isNextMBTNEnable(enable: Boolean) {
        _nextMBTNEnable.value = enable
    }


    //########## Progress MaterialTextView text decider
    private var _progressMTVTextDecider = MutableLiveData<String>()
    val progressMTVTextDecider: LiveData<String>
        get() = _progressMTVTextDecider


    //########## Progress AlertDialog open
    private var _progressADOpen = MutableLiveData<Boolean>()
    val progressADOpen: LiveData<Boolean>
        get() = _progressADOpen


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Success AlertDialog open
    private var _successADOpen = MutableLiveData<Boolean>()
    val successADOpen: LiveData<Boolean>
        get() = _successADOpen


    //########## Continue MaterialButton click
    private var _continueMBTNClick = MutableLiveData<Event<Boolean>>()
    val continueMBTNClick: LiveData<Event<Boolean>>
        get() = _continueMBTNClick

    fun onContinueMBTNClick() {
        _continueMBTNClick.value = Event(true)
    }


    //########## Log in MaterialTextView click
    private var _logInMTVClick = MutableLiveData<Event<Boolean>>()
    val logInMTVClick: LiveData<Event<Boolean>>
        get() = _logInMTVClick

    fun onLogInMTVClick() {
        _logInMTVClick.value = Event(true)
    }


    //########## Success AlertDialog ProgressBar gone
    private var _successADPBGone = MutableLiveData<Boolean>()
    val successADPBGone: LiveData<Boolean>
        get() = _successADPBGone


    //########## Success AlertDialog ImageView gone
    private var _successADIVGone = MutableLiveData<Boolean>()
    val successADIVGone: LiveData<Boolean>
        get() = _successADIVGone


    //########## Email verification successful
    private var _emailVerificationSuccessful = MutableLiveData<Boolean>()
    val emailVerificationSuccessful: LiveData<Boolean>
        get() = _emailVerificationSuccessful


    //########## Continue MaterialButton enable
    private var _continueMBTNEnable = MutableLiveData<Boolean>()
    val continueMBTNEnable: LiveData<Boolean>
        get() = _continueMBTNEnable

    fun isContinueMBTNEnable(enable: Boolean) {
        _continueMBTNEnable.value = enable
    }


    //########## Sign up
    fun signUp() {

        _progressMTVTextDecider.value = CREATE
        _progressADOpen.value = true

        Firebase.auth.createUserWithEmailAndPassword(
            emailETText.value!!,
            passwordETText.value!!
        ).addOnCompleteListener {

            if (it.isSuccessful) sendEmailVerification() else {
                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                _errorMessage.value = errorMessageAuth(it)
            }

        }

    }


    //########## Send email verification
    private fun sendEmailVerification() {

        _progressMTVTextDecider.value = SEND_VERIFICATION

        with(Firebase.auth.currentUser) {

            if (this != null) {

                if (!isEmailVerified) {

                    Firebase.auth.setLanguageCode(appLanguage)

                    sendEmailVerification().addOnCompleteListener {
                        _progressMTVTextDecider.value = ""
                        _progressADOpen.value = false

                        if (it.isSuccessful) _successADOpen.value = true else _errorMessage.value =
                            errorMessageVoid(it)
                    }

                } else {
                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    _errorMessage.value = Event("")
                }

            } else {
                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                _errorMessage.value = Event("")
            }

        }

    }


    //########## Is email verified
    fun isEmailVerified() {

        _successADIVGone.value = true
        _successADPBGone.value = false

        with(Firebase.auth.currentUser) {

            if (this != null) {

                reload()

                with(Firebase.auth) {

                    signOut()

                    signInWithEmailAndPassword(
                        emailETText.value!!,
                        passwordETText.value!!
                    ).addOnCompleteListener {

                        _continueMBTNEnable.value = true

                        _successADIVGone.value = false
                        _successADPBGone.value = true

                        if (it.isSuccessful) {

                            if (it.result != null)
                                if (it.result!!.user != null)
                                    if (it.result!!.user!!.isEmailVerified) {
                                        _successADOpen.value = false
                                        _emailVerificationSuccessful.value = true
                                    } else _errorMessage.value = Event(UNVERIFIED_EMAIL)
                                else _errorMessage.value = Event("")
                            else _errorMessage.value = Event("")

                        } else _errorMessage.value = errorMessageAuth(it)

                    }

                }

            } else {
                _continueMBTNEnable.value = true

                _successADIVGone.value = false
                _successADPBGone.value = true

                _errorMessage.value = Event("")
            }

        }

    }


    init {
        _nextMBTNEnable.value = false
        _successADPBGone.value = true
    }

}
