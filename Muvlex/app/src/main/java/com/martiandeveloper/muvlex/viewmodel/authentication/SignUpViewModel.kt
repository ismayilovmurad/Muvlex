package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.appLanguage

class SignUpViewModel : ViewModel() {

    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Continue MaterialButton click
    private var _continueMBTNClick = MutableLiveData<Event<Boolean>>()
    val continueMBTNClick: LiveData<Event<Boolean>>
        get() = _continueMBTNClick

    fun onContinueMBTNClick() {
        _continueMBTNClick.value = Event(true)
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


    //########## Log in MaterialTextView click
    private var _logInMTVClick = MutableLiveData<Event<Boolean>>()
    val logInMTVClick: LiveData<Event<Boolean>>
        get() = _logInMTVClick

    fun onLogInMTVClick() {
        _logInMTVClick.value = Event(true)
    }


    //########## Next MaterialButton enable
    private var _nextMBTNEnable = MutableLiveData<Boolean>()
    val nextMBTNEnable: LiveData<Boolean>
        get() = _nextMBTNEnable

    fun isNextMBTNEnable(enable: Boolean) {
        _nextMBTNEnable.value = enable
    }


    //########## Sign up
    fun signUp() {

        _progressMTVTextDecider.value = "create"
        _progressADOpen.value = true

        Firebase.auth.createUserWithEmailAndPassword(
            emailETText.value!!,
            passwordETText.value!!
        ).addOnCompleteListener {

            _progressMTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful) {
                sendEmailVerification()
            } else {

                _signUpSuccessful.value = false

                if (it.exception != null) {

                    if (it.exception!!.localizedMessage != null) {
                        _errorMessage.value = Event(it.exception!!.localizedMessage!!.toString())
                    } else {
                        _errorMessage.value = Event("")
                    }

                } else {
                    _errorMessage.value = Event("")
                }

            }

        }

    }


    //########## Send email verification
    private fun sendEmailVerification() {

        _progressMTVTextDecider.value = "verification"
        _progressADOpen.value = true

        val user = Firebase.auth.currentUser

        if (user != null) {

            if (!user.isEmailVerified) {

                Firebase.auth.setLanguageCode(appLanguage)

                user.sendEmailVerification().addOnCompleteListener {

                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    if (it.isSuccessful) {
                        _signUpSuccessful.value = true
                    } else {

                        _signUpSuccessful.value = false

                        if (it.exception != null) {

                            if (it.exception!!.localizedMessage != null) {
                                _errorMessage.value =
                                    Event(it.exception!!.localizedMessage!!.toString())
                            } else {
                                _errorMessage.value = Event("")
                            }

                        } else {
                            _errorMessage.value = Event("")
                        }

                    }

                }

            } else {
                _signUpSuccessful.value = false
                _errorMessage.value = Event("")
            }

        } else {
            _signUpSuccessful.value = false
            _errorMessage.value = Event("")
        }

    }


    //########## Progress MaterialTextView text decider
    private var _progressMTVTextDecider = MutableLiveData<String>()
    val progressMTVTextDecider: LiveData<String>
        get() = _progressMTVTextDecider


    //########## Progress AlertDialog open
    private var _progressADOpen = MutableLiveData<Boolean>()
    val progressADOpen: LiveData<Boolean>
        get() = _progressADOpen


    //########## Sign up successful
    private var _signUpSuccessful = MutableLiveData<Boolean>()
    val signUpSuccessful: LiveData<Boolean>
        get() = _signUpSuccessful


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Success AlertDialog open
    private var _successADOpen = MutableLiveData<Boolean>()
    val successADOpen: LiveData<Boolean>
        get() = _successADOpen

    fun isSuccessADOpen(open: Boolean) {
        _successADOpen.value = open
    }


    //########## Is email verified
    fun isEmailVerified() {

        _successADIVGone.value = true
        _successADPBGone.value = false

        val user = Firebase.auth.currentUser

        if (user != null) {

            user.reload()

            Firebase.auth.signOut()

            Firebase.auth.signInWithEmailAndPassword(
                emailETText.value!!,
                passwordETText.value!!
            ).addOnCompleteListener {

                _successADIVGone.value = false
                _successADPBGone.value = true

                if (it.isSuccessful) {

                    if (it.result != null) {

                        if (it.result!!.user != null) {

                            if (it.result!!.user!!.isEmailVerified) {
                                _emailVerificationSuccessful.value = true
                            } else {
                                _emailVerificationSuccessful.value = false
                                _errorMessage.value = Event("unverified")
                            }

                        } else {
                            _emailVerificationSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _emailVerificationSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {

                    _emailVerificationSuccessful.value = false

                    if (it.exception != null) {

                        if (it.exception!!.localizedMessage != null) {
                            _errorMessage.value =
                                Event(it.exception!!.localizedMessage!!.toString())
                        } else {
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _errorMessage.value = Event("")
                    }

                }

            }

        } else {
            _successADIVGone.value = false
            _successADPBGone.value = true

            _emailVerificationSuccessful.value = false
            _errorMessage.value = Event("")
        }

    }


    //########## Email verification successful
    private var _emailVerificationSuccessful = MutableLiveData<Boolean>()
    val emailVerificationSuccessful: LiveData<Boolean>
        get() = _emailVerificationSuccessful


    //########## Success AlertDialog ProgressBar gone
    private var _successADPBGone = MutableLiveData<Boolean>()
    val successADPBGone: LiveData<Boolean>
        get() = _successADPBGone

    fun isSuccessADPBGone(gone: Boolean) {
        _successADPBGone.value = gone
    }


    //########## Success AlertDialog ImageView gone
    private var _successADIVGone = MutableLiveData<Boolean>()
    val successADIVGone: LiveData<Boolean>
        get() = _successADIVGone

    fun isSuccessADIVGone(gone: Boolean) {
        _successADIVGone.value = gone
    }


    //########## Continue MaterialButton enable
    private var _continueMBTNEnable = MutableLiveData<Boolean>()
    val continueMBTNEnable: LiveData<Boolean>
        get() = _continueMBTNEnable

    fun isContinueMBTNEnable(enable: Boolean) {
        _continueMBTNEnable.value = enable
    }

}
