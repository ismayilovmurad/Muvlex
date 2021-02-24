package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.*

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

    fun isSuccessADOpen(open: Boolean) {
        _successADOpen.value = open
    }


    //########## Okay MaterialButton click
    private var _okayMBTNClick = MutableLiveData<Event<Boolean>>()
    val okayMBTNClick: LiveData<Event<Boolean>>
        get() = _okayMBTNClick

    fun onOkayMBTNClick() {
        _okayMBTNClick.value = Event(true)
    }


    //########## Send password reset email
    fun sendPasswordResetEmail(email: String?) {
        val email1: String

        if (email == null) {
            _progressADOpen.value = true
            email1 = emailOrUsernameETText.value!!
        } else email1 = email

        _progressMTVTextDecider.value = SEND

        with(Firebase.auth) {

            setLanguageCode(appLanguage)

            sendPasswordResetEmail(
                email1
            ).addOnCompleteListener {
                _progressMTVTextDecider.value = ""
                _progressADOpen.value = false

                if (it.isSuccessful) isSuccessADOpen(true) else _errorMessage.value =
                    errorMessageVoid(it)
            }

        }

    }


    //########## Is username exists
    fun isUsernameExists() {

        _progressMTVTextDecider.value = CHECK_USERNAME
        _progressADOpen.value = true

        Firebase.firestore.collection("users")
            .whereEqualTo("username", emailOrUsernameETText.value).get().addOnCompleteListener {

                if (it.isSuccessful) {

                    for (i in it.result!!) {

                        if (i.getString("username") == emailOrUsernameETText.value) getUserEmail(it.result!!.documents[0].id)
                        else {
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

                if (it.isSuccessful) {

                    if (it.result != null)

                        if (it.result != null) sendPasswordResetEmail(
                            it.result!!.get("email").toString()
                        )
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

                } else {
                    _progressMTVTextDecider.value = ""
                    _progressADOpen.value = false

                    _errorMessage.value = errorMessageDocument(it)
                }

            }

    }


    init {
        _continueMBTNEnable.value = false
    }

}
