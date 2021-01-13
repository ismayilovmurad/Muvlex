package com.martiandeveloper.muvlex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event

class LogInViewModel : ViewModel() {

    //########## Progress text
    private var _progressText = MutableLiveData<String>()
    val progressText: LiveData<String>
        get() = _progressText

    fun setProgressText(text: String) {
        _progressText.value = text
    }


    //########## Email or username AutoCompleteTextView content
    val emailOrUsernameACTContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Password EditText content
    val passwordETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## On log in button click
    private var _onLogInButtonClick = MutableLiveData<Event<Boolean>>()
    val onLogInButtonClick: LiveData<Event<Boolean>>
        get() = _onLogInButtonClick

    fun onLogInButtonClick() {
        _onLogInButtonClick.value = Event(true)
    }


    //########## On get help logging in TextView click
    private var _onGetHelpTextViewClick = MutableLiveData<Event<Boolean>>()
    val onGetHelpTextViewClick: LiveData<Event<Boolean>>
        get() = _onGetHelpTextViewClick

    fun onGetHelpTextViewClick() {
        _onGetHelpTextViewClick.value = Event(true)
    }


    //########## On sign up TextView click
    private var _onSignUpTextViewClick = MutableLiveData<Event<Boolean>>()
    val onSignUpTextViewClick: LiveData<Event<Boolean>>
        get() = _onSignUpTextViewClick

    fun onSignUpTextViewClick() {
        _onSignUpTextViewClick.value = Event(true)
    }


    //########## Is log in button enable
    private var _isLogInButtonEnable = MutableLiveData<Boolean>()
    val isLogInButtonEnable: LiveData<Boolean>
        get() = _isLogInButtonEnable

    fun setLogInButtonEnable(enable: Boolean) {
        _isLogInButtonEnable.value = enable
    }


    //########## Log in
    fun logIn() {

        _progressTextDecider.value = "login"
        _isProgressDialogOpen.value = true

        Firebase.auth.signInWithEmailAndPassword(
            emailOrUsernameACTContent.value!!,
            passwordETContent.value!!
        ).addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {

                if (it.result != null) {

                    if (it.result!!.user != null) {
                        checkEmailVerification(it.result!!.user!!)
                    } else {
                        _isLogInSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {
                    _isLogInSuccessful.value = false
                    _errorMessage.value = Event("")
                }

            } else {

                _isLogInSuccessful.value = false

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

    private fun checkEmailVerification(user: FirebaseUser) {

        if (user.isEmailVerified) {
            checkIfUserHasUsername(user)
        } else {
            _isLogInSuccessful.value = false
            _errorMessage.value = Event("Email is not verified")
        }

    }

    private fun checkIfUserHasUsername(user: FirebaseUser) {

        _progressTextDecider.value = "load"
        _isProgressDialogOpen.value = true

        Firebase.firestore.collection("users").document(user.uid).get().addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {

                if (it.result != null) {

                    if (it.result!!.get("username") != null) {
                        _isLogInSuccessful.value = true
                    } else {
                        _isLogInSuccessful.value = false
                        _errorMessage.value = Event("Username not found")
                    }

                } else {
                    _isLogInSuccessful.value = false
                    _errorMessage.value = Event("")
                }

            } else {

                _isLogInSuccessful.value = false

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


    //########## Progress text decider
    private var _progressTextDecider = MutableLiveData<String>()
    val progressTextDecider: LiveData<String>
        get() = _progressTextDecider


    //########## Is progress dialog open
    private var _isProgressDialogOpen = MutableLiveData<Boolean>()
    val isProgressDialogOpen: LiveData<Boolean>
        get() = _isProgressDialogOpen


    //########## Is log in successful
    private var _isLogInSuccessful = MutableLiveData<Boolean>()
    val isLogInSuccessful: LiveData<Boolean>
        get() = _isLogInSuccessful


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Check if username exists
    fun checkIfUsernameExists() {

        _progressTextDecider.value = "check_username"
        _isProgressDialogOpen.value = true

        val query =
            Firebase.firestore.collection("users")
                .whereEqualTo("username", emailOrUsernameACTContent.value)

        query.get().addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == emailOrUsernameACTContent.value) {
                        getUserEmail(it.result!!.documents[0].id)
                    } else {
                        _isLogInSuccessful.value = false
                        _errorMessage.value = Event("no_account")
                    }

                }

                if (it.result?.size() == 0) {
                    _isLogInSuccessful.value = false
                    _errorMessage.value = Event("no_account")
                }

            } else {

                _isLogInSuccessful.value = false

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

    private fun getUserEmail(uid: String) {

        _progressTextDecider.value = "load"
        _isProgressDialogOpen.value = true

        Firebase.firestore.collection("users").document(uid)
            .get().addOnCompleteListener {

                _progressTextDecider.value = ""
                _isProgressDialogOpen.value = false

                if (it.isSuccessful) {

                    if (it.result != null) {

                        if (it.result != null) {
                            loginWithUsername(
                                it.result!!.get("email").toString()
                            )
                        } else {
                            _isLogInSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _isLogInSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {

                    _isLogInSuccessful.value = false

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

    }

    private fun loginWithUsername(email: String) {

        _progressTextDecider.value = "login"
        _isProgressDialogOpen.value = true

        Firebase.auth.signInWithEmailAndPassword(
            email,
            passwordETContent.value!!
        ).addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {

                if (it.result != null) {

                    if (it.result!!.user != null) {
                        _isLogInSuccessful.value = true
                    } else {
                        _isLogInSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {
                    _isLogInSuccessful.value = false
                    _errorMessage.value = Event("")
                }

            } else {

                _isLogInSuccessful.value = false

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


    //########## On resend button click
    private var _onResendButtonClick = MutableLiveData<Event<Boolean>>()
    val onResendButtonClick: LiveData<Event<Boolean>>
        get() = _onResendButtonClick

    fun onResendButtonClick() {
        _onResendButtonClick.value = Event(true)
    }


    //########## On okay button click
    private var _onOkayButtonClick = MutableLiveData<Event<Boolean>>()
    val onOkayButtonClick: LiveData<Event<Boolean>>
        get() = _onOkayButtonClick

    fun onOkayButtonClick() {
        _onOkayButtonClick.value = Event(true)
    }


    //########## Is error dialog open
    private var _isErrorDialogOpen = MutableLiveData<Boolean>()
    val isErrorDialogOpen: LiveData<Boolean>
        get() = _isErrorDialogOpen

    fun setErrorDialogOpen(open: Boolean) {
        _isErrorDialogOpen.value = open
    }


    //########## Resend verification
    fun resendVerification() {

        _isErrorDialogImageGone.value = true
        _isErrorDialogProgressGone.value = false

        val user = Firebase.auth.currentUser

        if (user != null) {

            if (!user.isEmailVerified) {

                user.sendEmailVerification().addOnCompleteListener {

                    _isErrorDialogImageGone.value = false
                    _isErrorDialogProgressGone.value = true

                    if (it.isSuccessful) {
                        _isResendSuccessful.value = true
                    } else {

                        _isResendSuccessful.value = false

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
                _isErrorDialogImageGone.value = false
                _isErrorDialogProgressGone.value = true

                _isResendSuccessful.value = false
                _errorMessage.value = Event("already_verified")
            }

        } else {
            _isErrorDialogImageGone.value = false
            _isErrorDialogProgressGone.value = true

            _isResendSuccessful.value = false
            _errorMessage.value = Event("")
        }

    }

    //########## Is resend successful
    private var _isResendSuccessful = MutableLiveData<Boolean>()
    val isResendSuccessful: LiveData<Boolean>
        get() = _isResendSuccessful


    //########## Is error dialog progress gone
    private var _isErrorDialogProgressGone = MutableLiveData<Boolean>()
    val isErrorDialogProgressGone: LiveData<Boolean>
        get() = _isErrorDialogProgressGone

    fun setIsErrorDialogProgressGone(gone: Boolean) {
        _isErrorDialogProgressGone.value = gone
    }


    //########## Is error dialog image gone
    private var _isErrorDialogImageGone = MutableLiveData<Boolean>()
    val isErrorDialogImageGone: LiveData<Boolean>
        get() = _isErrorDialogImageGone

    fun setIsErrorDialogImageGone(gone: Boolean) {
        _isErrorDialogImageGone.value = gone
    }


    //########## Is resend and okay buttons enable
    private var _isResendAndOkayButtonsEnable = MutableLiveData<Boolean>()
    val isResendAndOkayButtonsEnable: LiveData<Boolean>
        get() = _isResendAndOkayButtonsEnable

    fun setIsResendAndOkayButtonsEnable(enable: Boolean) {
        _isResendAndOkayButtonsEnable.value = enable
    }

}
