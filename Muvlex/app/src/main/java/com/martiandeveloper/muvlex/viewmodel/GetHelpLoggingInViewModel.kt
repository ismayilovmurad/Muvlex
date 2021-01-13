package com.martiandeveloper.muvlex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event

class GetHelpLoggingInViewModel : ViewModel() {

    //########## Progress text
    private var _progressText = MutableLiveData<String>()
    val progressText: LiveData<String>
        get() = _progressText

    fun setProgressText(text: String) {
        _progressText.value = text
    }


    //########## Email or username EditText content
    val emailOrUsernameETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## On continue button click
    private var _onContinueButtonClick = MutableLiveData<Event<Boolean>>()
    val onContinueButtonClick: LiveData<Event<Boolean>>
        get() = _onContinueButtonClick

    fun onContinueButtonClick() {
        _onContinueButtonClick.value = Event(true)
    }


    //########## Is continue button enable
    private var _isContinueButtonEnable = MutableLiveData<Boolean>()
    val isContinueButtonEnable: LiveData<Boolean>
        get() = _isContinueButtonEnable

    fun setLogInButtonEnable(enable: Boolean) {
        _isContinueButtonEnable.value = enable
    }


    //########## Send reset password
    fun sendResetPassword() {

        _progressTextDecider.value = "send"
        _isProgressDialogOpen.value = true

        Firebase.auth.sendPasswordResetEmail(
            emailOrUsernameETContent.value!!
        ).addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {
                _isSendingSuccessful.value = true
            } else {

                _isSendingSuccessful.value = false

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
    private var _isSendingSuccessful = MutableLiveData<Boolean>()
    val isSendingSuccessful: LiveData<Boolean>
        get() = _isSendingSuccessful


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
                .whereEqualTo("username", emailOrUsernameETContent.value)

        query.get().addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == emailOrUsernameETContent.value) {
                        getUserEmail(it.result!!.documents[0].id)
                    } else {
                        _isSendingSuccessful.value = false
                        _errorMessage.value = Event("no_account")
                    }

                }

                if (it.result?.size() == 0) {
                    _isSendingSuccessful.value = false
                    _errorMessage.value = Event("no_account")
                }

            } else {

                _isSendingSuccessful.value = false

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
                            sendResetPassword(it.result!!.get("email").toString())
                        } else {
                            _isSendingSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _isSendingSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {

                    _isSendingSuccessful.value = false

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

    private fun sendResetPassword(email: String) {

        _progressTextDecider.value = "send"
        _isProgressDialogOpen.value = true

        Firebase.auth.sendPasswordResetEmail(
            email
        ).addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {
                _isSendingSuccessful.value = true
            } else {

                _isSendingSuccessful.value = false

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


    //########## On okay button click
    private var _onOkayButtonClick = MutableLiveData<Event<Boolean>>()
    val onOkayButtonClick: LiveData<Event<Boolean>>
        get() = _onOkayButtonClick

    fun onOkayButtonClick() {
        _onOkayButtonClick.value = Event(true)
    }


    //########## Is success dialog open
    private var _isSuccessDialogOpen = MutableLiveData<Boolean>()
    val isSuccessDialogOpen: LiveData<Boolean>
        get() = _isSuccessDialogOpen

    fun setSuccessDialogOpen(open: Boolean) {
        _isSuccessDialogOpen.value = open
    }

}
