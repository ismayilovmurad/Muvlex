package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
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


    //########## Get help MaterialTextView click
    private var _getHelpMTVClick = MutableLiveData<Event<Boolean>>()
    val getHelpMTVClick: LiveData<Event<Boolean>>
        get() = _getHelpMTVClick

    fun onGetHelpMTVClick() {
        _getHelpMTVClick.value = Event(true)
    }


    //########## Sign up MaterialTextView click
    private var _signUpMTVClick = MutableLiveData<Event<Boolean>>()
    val signUpMTVClick: LiveData<Event<Boolean>>
        get() = _signUpMTVClick

    fun onSignUpMTVClick() {
        _signUpMTVClick.value = Event(true)
    }


    //########## Log in MaterialButton enable
    private var _logInMBTNEnable = MutableLiveData<Boolean>()
    val logInMBTNEnable: LiveData<Boolean>
        get() = _logInMBTNEnable

    fun isLogInMBTNEnable(enable: Boolean) {
        _logInMBTNEnable.value = enable
    }


    //########## Log in
    fun logIn() {

        _progressMTVTextDecider.value = "login"
        _progressADOpen.value = true

        Firebase.auth.signInWithEmailAndPassword(
            emailOrUsernameACTText.value!!,
            passwordETText.value!!
        ).addOnCompleteListener {

            _progressMTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful)

                if (it.result != null)

                    if (it.result!!.user != null) checkEmailVerification(it.result!!.user!!)
                    else {
                        _logInSuccessful.value = false
                        _errorMessage.value = Event("")
                    }
                else {
                    _logInSuccessful.value = false
                    _errorMessage.value = Event("")
                }
            else {
                _logInSuccessful.value = false
                _errorMessage.value = errorMessageAuth(it)
            }

        }

    }


    //########## Check email verification
    private fun checkEmailVerification(user: FirebaseUser) {

        if (user.isEmailVerified) hasUserUsername(user) else {
            _logInSuccessful.value = false
            _errorMessage.value = Event("Email is not verified")
        }

    }


    //########## Has user username
    private fun hasUserUsername(user: FirebaseUser) {

        _progressMTVTextDecider.value = "load"
        _progressADOpen.value = true

        Firebase.firestore.collection("users").document(user.uid).get().addOnCompleteListener {

            _progressMTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful)

                if (it.result != null)

                    if (it.result!!.get("username") != null) _logInSuccessful.value = true else {
                        _logInSuccessful.value = false
                        _errorMessage.value = Event("Username not found")
                    }
                else {
                    _logInSuccessful.value = false
                    _errorMessage.value = Event("")
                }
            else {
                _logInSuccessful.value = false
                _errorMessage.value = errorMessageDocument(it)
            }

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


    //########## Log in successful
    private var _logInSuccessful = MutableLiveData<Boolean>()
    val logInSuccessful: LiveData<Boolean>
        get() = _logInSuccessful


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Is username exists
    fun isUsernameExists() {

        _progressMTVTextDecider.value = "check_username"
        _progressADOpen.value = true

        Firebase.firestore.collection("users")
            .whereEqualTo("username", emailOrUsernameACTText.value).get().addOnCompleteListener {

                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                if (it.isSuccessful) {

                    for (i in it.result!!) {

                        if (i.getString("username") == emailOrUsernameACTText.value) getUserEmail(it.result!!.documents[0].id) else {
                            _logInSuccessful.value = false
                            _errorMessage.value = Event("no_account")
                        }

                    }

                    if (it.result?.size() == 0) {
                        _logInSuccessful.value = false
                        _errorMessage.value = Event("no_account")
                    }

                } else {
                    _logInSuccessful.value = false
                    _errorMessage.value = errorMessageQuery(it)
                }

            }

    }


    //########## Get user email
    private fun getUserEmail(uid: String) {

        _progressMTVTextDecider.value = "load"
        _progressADOpen.value = true

        Firebase.firestore.collection("users").document(uid)
            .get().addOnCompleteListener {

                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                if (it.isSuccessful)

                    if (it.result != null)

                        if (it.result != null) loginWithUsername(
                            it.result!!.get("email").toString()
                        ) else {
                            _logInSuccessful.value = false
                            _errorMessage.value = Event("")
                        }
                    else {
                        _logInSuccessful.value = false
                        _errorMessage.value = Event("")
                    }
                else {
                    _logInSuccessful.value = false
                    _errorMessage.value = errorMessageDocument(it)
                }

            }

    }


    //########## Get user email
    private fun loginWithUsername(email: String) {

        _progressMTVTextDecider.value = "login"
        _progressADOpen.value = true

        Firebase.auth.signInWithEmailAndPassword(
            email,
            passwordETText.value!!
        ).addOnCompleteListener {

            _progressMTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful)

                if (it.result != null)

                    if (it.result!!.user != null) _logInSuccessful.value = true else {
                        _logInSuccessful.value = false
                        _errorMessage.value = Event("")
                    }
                else {
                    _logInSuccessful.value = false
                    _errorMessage.value = Event("")
                }
            else {
                _logInSuccessful.value = false
                _errorMessage.value = errorMessageAuth(it)
            }

        }

    }


    //########## Resend MaterialButton click
    private var _resendMBTNClick = MutableLiveData<Event<Boolean>>()
    val resendMBTNClick: LiveData<Event<Boolean>>
        get() = _resendMBTNClick

    fun onResendMBTNClick() {
        _resendMBTNClick.value = Event(true)
    }


    //########## Okay MaterialButton click
    private var _okayMBTNClick = MutableLiveData<Event<Boolean>>()
    val okayMBTNClick: LiveData<Event<Boolean>>
        get() = _okayMBTNClick

    fun onOkayMBTNClick() {
        _okayMBTNClick.value = Event(true)
    }


    //########## Error AlertDialog open
    private var _errorADOpen = MutableLiveData<Boolean>()
    val errorADOpen: LiveData<Boolean>
        get() = _errorADOpen

    fun isErrorADOpen(open: Boolean) {
        _errorADOpen.value = open
    }


    //########## Resend email verification
    fun resendEmailVerification() {

        _errorADIVGone.value = true
        _errorADPBGone.value = false

        val user = Firebase.auth.currentUser

        if (user != null)

            if (!user.isEmailVerified) {

                Firebase.auth.setLanguageCode(appLanguage)

                user.sendEmailVerification().addOnCompleteListener {
                    _errorADIVGone.value = false
                    _errorADPBGone.value = true

                    _resendSuccessful.value = Event(it.isSuccessful)
                    if (!_resendSuccessful.value!!.peekContent()) _errorMessage.value =
                        errorMessageVoid(it)
                }

            } else {
                _errorADIVGone.value = false
                _errorADPBGone.value = true

                _resendSuccessful.value = Event(false)
                _errorMessage.value = Event("already_verified")
            }
        else {
            _errorADIVGone.value = false
            _errorADPBGone.value = true

            _resendSuccessful.value = Event(false)
            _errorMessage.value = Event("")
        }

    }


    //########## Resend successful
    private var _resendSuccessful = MutableLiveData<Event<Boolean>>()
    val resendSuccessful: LiveData<Event<Boolean>>
        get() = _resendSuccessful


    //########## Error AlertDialog ProgressBar gone
    private var _errorADPBGone = MutableLiveData<Boolean>()
    val errorADPBGone: LiveData<Boolean>
        get() = _errorADPBGone

    fun isErrorADPBGone(gone: Boolean) {
        _errorADPBGone.value = gone
    }


    //########## Error AlertDialog ImageView gone
    private var _errorADIVGone = MutableLiveData<Boolean>()
    val errorADIVGone: LiveData<Boolean>
        get() = _errorADIVGone

    fun isErrorADIVGone(gone: Boolean) {
        _errorADIVGone.value = gone
    }


    //########## Resend and okay MaterialButtons enable
    private var _resendAndOkayMBTNSEnable = MutableLiveData<Boolean>()
    val resendAndOkayMBTNSEnable: LiveData<Boolean>
        get() = _resendAndOkayMBTNSEnable

    fun isResendAndOkayMBTNSEnable(enable: Boolean) {
        _resendAndOkayMBTNSEnable.value = enable
    }

}
