package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.appLanguage

class GetHelpViewModel : ViewModel() {

    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Email or username EditText text
    val emailOrUsernameETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Continue MaterialButton click
    private var _continueMBTNClick = MutableLiveData<Event<Boolean>>()
    val continueMBTNClick: LiveData<Event<Boolean>>
        get() = _continueMBTNClick

    fun onContinueMBTNClick() {
        _continueMBTNClick.value = Event(true)
    }


    //########## Continue MaterialButton enable
    private var _continueMBTNEnable = MutableLiveData<Boolean>()
    val continueMBTNEnable: LiveData<Boolean>
        get() = _continueMBTNEnable

    fun isContinueMBTNEnable(enable: Boolean) {
        _continueMBTNEnable.value = enable
    }


    //########## Send password reset email
    fun sendPasswordResetEmailForUsername() {

        _progressTVTextDecider.value = "send"
        _progressADOpen.value = true

        Firebase.auth.setLanguageCode(appLanguage)

        Firebase.auth.sendPasswordResetEmail(
            emailOrUsernameETText.value!!
        ).addOnCompleteListener {

            _progressTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful) {
                _sendPasswordResetEmailSuccessful.value = true
            } else {

                _sendPasswordResetEmailSuccessful.value = false

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


    //########## Progress TextView text decider
    private var _progressTVTextDecider = MutableLiveData<String>()
    val progressTVTextDecider: LiveData<String>
        get() = _progressTVTextDecider


    //########## Progress AlertDialog open
    private var _progressADOpen = MutableLiveData<Boolean>()
    val progressADOpen: LiveData<Boolean>
        get() = _progressADOpen


    //########## Send password reset email successful
    private var _sendPasswordResetEmailSuccessful = MutableLiveData<Boolean>()
    val sendPasswordResetEmailSuccessful: LiveData<Boolean>
        get() = _sendPasswordResetEmailSuccessful


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Is username exists
    fun isUsernameExists() {

        _progressTVTextDecider.value = "check_username"
        _progressADOpen.value = true

        val query =
            Firebase.firestore.collection("users")
                .whereEqualTo("username", emailOrUsernameETText.value)

        query.get().addOnCompleteListener {

            _progressTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == emailOrUsernameETText.value) {
                        getUserEmail(it.result!!.documents[0].id)
                    } else {
                        _sendPasswordResetEmailSuccessful.value = false
                        _errorMessage.value = Event("no_account")
                    }

                }

                if (it.result?.size() == 0) {
                    _sendPasswordResetEmailSuccessful.value = false
                    _errorMessage.value = Event("no_account")
                }

            } else {

                _sendPasswordResetEmailSuccessful.value = false

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


    //########## Get user email
    private fun getUserEmail(uid: String) {

        _progressTVTextDecider.value = "load"
        _progressADOpen.value = true

        Firebase.firestore.collection("users").document(uid)
            .get().addOnCompleteListener {

                _progressTVTextDecider.value = ""
                _progressADOpen.value = false

                if (it.isSuccessful) {

                    if (it.result != null) {

                        if (it.result != null) {
                            sendPasswordResetEmailForUsername(it.result!!.get("email").toString())
                        } else {
                            _sendPasswordResetEmailSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _sendPasswordResetEmailSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {

                    _sendPasswordResetEmailSuccessful.value = false

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


    //########## Send password reset email for username
    private fun sendPasswordResetEmailForUsername(email: String) {

        _progressTVTextDecider.value = "send"
        _progressADOpen.value = true

        Firebase.auth.setLanguageCode(appLanguage)

        Firebase.auth.sendPasswordResetEmail(
            email
        ).addOnCompleteListener {

            _progressTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful) {
                _sendPasswordResetEmailSuccessful.value = true
            } else {

                _sendPasswordResetEmailSuccessful.value = false

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


    //########## Okay MaterialButton click
    private var _okayMBTNClick = MutableLiveData<Event<Boolean>>()
    val okayMBTNClick: LiveData<Event<Boolean>>
        get() = _okayMBTNClick

    fun onOkayMBTNClick() {
        _okayMBTNClick.value = Event(true)
    }


    //########## Success AlertDialog open
    private var _successADOpen = MutableLiveData<Boolean>()
    val successADOpen: LiveData<Boolean>
        get() = _successADOpen

    fun isSuccessADOpen(open: Boolean) {
        _successADOpen.value = open
    }

}
