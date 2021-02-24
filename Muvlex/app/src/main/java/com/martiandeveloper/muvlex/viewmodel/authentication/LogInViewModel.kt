package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.*

class LogInViewModel : ViewModel() {

    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Email or username AutoCompleteTextView text
    val emailOrUsernameACTText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Password EditText text
    val passwordETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Log in MaterialButton click
    private var _logInMBTNClick = MutableLiveData<Event<Boolean>>()
    val logInMBTNClick: LiveData<Event<Boolean>>
        get() = _logInMBTNClick

    fun onLogInMBTNClick() {
        _logInMBTNClick.value = Event(true)
    }


    //########## Log in MaterialButton enable
    private var _logInMBTNEnable = MutableLiveData<Boolean>()
    val logInMBTNEnable: LiveData<Boolean>
        get() = _logInMBTNEnable

    fun isLogInMBTNEnable(enable: Boolean) {
        _logInMBTNEnable.value = enable
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


    //########## Log in successful
    private var _logInSuccessful = MutableLiveData<Boolean>()
    val logInSuccessful: LiveData<Boolean>
        get() = _logInSuccessful


    //########## Error AlertDialog open
    private var _errorADOpen = MutableLiveData<Boolean>()
    val errorADOpen: LiveData<Boolean>
        get() = _errorADOpen

    private fun isErrorADOpen(open: Boolean) {
        if (!open) Firebase.auth.signOut()
        _errorADOpen.value = open
    }


    //########## Okay MaterialButton click
    fun onOkayMBTNClick() {
        isErrorADOpen(false)
    }


    //########## Sign up MaterialTextView click
    private var _signUpMTVClick = MutableLiveData<Event<Boolean>>()
    val signUpMTVClick: LiveData<Event<Boolean>>
        get() = _signUpMTVClick

    fun onSignUpMTVClick() {
        _signUpMTVClick.value = Event(true)
    }


    //########## Error AlertDialog ProgressBar gone
    private var _errorADPBGone = MutableLiveData<Boolean>()
    val errorADPBGone: LiveData<Boolean>
        get() = _errorADPBGone


    //########## Error AlertDialog ImageView gone
    private var _errorADIVGone = MutableLiveData<Boolean>()
    val errorADIVGone: LiveData<Boolean>
        get() = _errorADIVGone


    //########## Resend successful
    private var _resendSuccessful = MutableLiveData<Event<Boolean>>()
    val resendSuccessful: LiveData<Event<Boolean>>
        get() = _resendSuccessful


    //########## Resend and okay MaterialButtons enable
    private var _resendAndOkayMBTNSEnable = MutableLiveData<Boolean>()
    val resendAndOkayMBTNSEnable: LiveData<Boolean>
        get() = _resendAndOkayMBTNSEnable

    fun isResendAndOkayMBTNSEnable(enable: Boolean) {
        _resendAndOkayMBTNSEnable.value = enable
    }


    //########## Get help MaterialTextView click
    private var _getHelpMTVClick = MutableLiveData<Event<Boolean>>()
    val getHelpMTVClick: LiveData<Event<Boolean>>
        get() = _getHelpMTVClick

    fun onGetHelpMTVClick() {
        _getHelpMTVClick.value = Event(true)
    }


    //########## Resend MaterialButton click
    private var _resendMBTNClick = MutableLiveData<Event<Boolean>>()
    val resendMBTNClick: LiveData<Event<Boolean>>
        get() = _resendMBTNClick

    fun onResendMBTNClick() {
        _resendMBTNClick.value = Event(true)
    }


    //########## Log in
    fun logInUser() {

        _progressMTVTextDecider.value = LOGIN
        _progressADOpen.value = true

        Firebase.auth.signInWithEmailAndPassword(
            emailOrUsernameACTText.value!!,
            passwordETText.value!!
        ).addOnCompleteListener {

            if (it.isSuccessful)

                if (it.result != null)

                    if (it.result!!.user != null) checkEmailVerification()
                    else {
                        _progressMTVTextDecider.value = ""
                        _progressADOpen.value = false

                        _errorMessage.value = Event("")
                    }
                else {
                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    _errorMessage.value = Event("")
                }
            else {
                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                _errorMessage.value = errorMessageAuth(it)
            }

        }

    }


    //########## Check email verification
    private fun checkEmailVerification() {

        with(Firebase.auth.currentUser) {

            if (this != null) {

                if (isEmailVerified) hasUserUsername() else {
                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    isErrorADOpen(true)
                }

            } else {
                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                _errorMessage.value = Event("")
            }

        }

    }


    //########## Has user username
    private fun hasUserUsername() {

        _progressMTVTextDecider.value = LOAD

        with(Firebase.auth.currentUser) {

            if (this != null) {

                Firebase.firestore.collection("users").document(uid).get()
                    .addOnCompleteListener {

                        _progressMTVTextDecider.value = ""
                        _progressADOpen.value = false

                        if (it.isSuccessful)
                            if (it.result != null)
                                if (it.result!!.get("username") != null) _logInSuccessful.value =
                                    true else _errorMessage.value = Event(USERNAME_NOT_FOUND)
                            else _errorMessage.value = Event("")
                        else _errorMessage.value = errorMessageDocument(it)

                    }

            } else {
                _errorMessage.value = Event("")
            }

        }

    }


    //########## Is username exists
    fun isUsernameExists() {

        _progressMTVTextDecider.value = CHECK_USERNAME
        _progressADOpen.value = true

        Firebase.firestore.collection("users")
            .whereEqualTo("username", emailOrUsernameACTText.value).get().addOnCompleteListener {

                if (it.isSuccessful) {

                    for (i in it.result!!) {

                        if (i.getString("username") == emailOrUsernameACTText.value) getUserEmail(it.result!!.documents[0].id) else {
                            _progressMTVTextDecider.value = ""
                            _progressADOpen.value = false

                            _errorMessage.value = Event(INVALID_USER)
                        }

                    }

                    if (it.result?.size() == 0) {
                        _progressMTVTextDecider.value = ""
                        _progressADOpen.value = false

                        _errorMessage.value = Event(INVALID_USER)
                    }

                } else {
                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    _errorMessage.value = errorMessageQuery(it)
                }

            }

    }


    //########## Get user email
    private fun getUserEmail(uid: String) {

        _progressMTVTextDecider.value = LOAD

        Firebase.firestore.collection("users").document(uid)
            .get().addOnCompleteListener {

                if (it.isSuccessful)

                    if (it.result != null)

                        if (it.result != null) login(
                            it.result!!.get("email").toString()
                        ) else {
                            _progressMTVTextDecider.value = ""
                            _progressADOpen.value = false

                            _errorMessage.value = Event("")
                        }
                    else {
                        _progressMTVTextDecider.value = ""
                        _progressADOpen.value = false

                        _errorMessage.value = Event("")
                    }
                else {
                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    _errorMessage.value = errorMessageDocument(it)
                }

            }

    }


    //########## Get user email
    private fun login(email: String) {

        _progressMTVTextDecider.value = LOGIN

        Firebase.auth.signInWithEmailAndPassword(
            email,
            passwordETText.value!!
        ).addOnCompleteListener {

            _progressMTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful)
                if (it.result != null)
                    if (it.result!!.user != null) _logInSuccessful.value =
                        true else _errorMessage.value = Event("")
                else _errorMessage.value = Event("")
            else _errorMessage.value = errorMessageAuth(it)

        }

    }


    //########## Resend email verification
    fun resendEmailVerification() {

        _errorADIVGone.value = true
        _errorADPBGone.value = false

        with(Firebase.auth.currentUser) {

            if (this != null)

                if (!isEmailVerified) {

                    Firebase.auth.setLanguageCode(appLanguage)

                    sendEmailVerification().addOnCompleteListener {
                        _errorADIVGone.value = false
                        _errorADPBGone.value = true

                        if (it.isSuccessful) _resendSuccessful.value =
                            Event(true) else _errorMessage.value = errorMessageVoid(it)

                        isErrorADOpen(false)
                    }

                } else {
                    _errorADIVGone.value = false
                    _errorADPBGone.value = true

                    _errorMessage.value = Event(EMAIL_ALREADY_VERIFIED)
                }
            else {
                _errorADIVGone.value = false
                _errorADPBGone.value = true

                _errorMessage.value = Event("")
            }

        }

    }


    init {
        _logInMBTNEnable.value = false
        _errorADPBGone.value = true
    }

}
