package com.martiandeveloper.muvlex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event

class SignUpUsernameViewModel : ViewModel() {

    //########## Progress text
    private var _progressText = MutableLiveData<String>()
    val progressText: LiveData<String>
        get() = _progressText

    fun setProgressText(text: String) {
        _progressText.value = text
    }


    //########## Username error text
    private var _usernameErrorText = MutableLiveData<String>()
    val usernameErrorText: LiveData<String>
        get() = _usernameErrorText

    fun setUsernameErrorText(text: String) {
        _usernameErrorText.value = text
    }


    //########## Is username error gone
    private var _isUsernameErrorGone = MutableLiveData<Boolean>()
    val isUsernameErrorGone: LiveData<Boolean>
        get() = _isUsernameErrorGone

    fun setIsUsernameErrorGone(gone: Boolean) {
        _isUsernameErrorGone.value = gone
    }


    //########## Username EditText content
    val usernameETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Is username progress gone
    private var _isUsernameProgressGone = MutableLiveData<Boolean>()
    val isUsernameProgressGone: LiveData<Boolean>
        get() = _isUsernameProgressGone

    fun setIsUsernameProgressGone(gone: Boolean) {
        _isUsernameProgressGone.value = gone
    }


    //########## On next button click
    private var _onNextButtonClick = MutableLiveData<Event<Boolean>>()
    val onNextButtonClick: LiveData<Event<Boolean>>
        get() = _onNextButtonClick

    fun onNextButtonClick() {
        _onNextButtonClick.value = Event(true)
    }


    //########## On privacy policy TextView click
    private var _onPrivacyPolicyTextViewClick = MutableLiveData<Event<Boolean>>()
    val onPrivacyPolicyTextViewClick: LiveData<Event<Boolean>>
        get() = _onPrivacyPolicyTextViewClick

    fun onPrivacyPolicyTextViewClick() {
        _onPrivacyPolicyTextViewClick.value = Event(true)
    }


    //########## Is next button enable
    private var _isNextButtonEnable = MutableLiveData<Boolean>()
    val isNextButtonEnable: LiveData<Boolean>
        get() = _isNextButtonEnable

    fun setNextButtonEnable(enable: Boolean) {
        _isNextButtonEnable.value = enable
    }


    //########## Check username
    fun checkUsername() {

        _isUsernameProgressGone.value = false

        val query =
            Firebase.firestore.collection("users").whereEqualTo("username", usernameETContent.value)

        query.get().addOnCompleteListener {

            _isUsernameProgressGone.value = true

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == usernameETContent.value) {
                        _isUsernameAvailable.value = false
                    }

                }

                if (it.result?.size() == 0) {
                    _isUsernameAvailable.value = true
                }

            } else {

                _isUsernameAvailable.value = false

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


    //########## Is username available
    private var _isUsernameAvailable = MutableLiveData<Boolean>()
    val isUsernameAvailable: LiveData<Boolean>
        get() = _isUsernameAvailable


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Save username
    fun saveUsernameAndEmail() {

        _progressTextDecider.value = "check"
        _isProgressDialogOpen.value = true

        val query =
            Firebase.firestore.collection("users").whereEqualTo("username", usernameETContent.value)

        query.get().addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == usernameETContent.value) {
                        _isUsernameAvailable.value = false
                    }

                }

                if (it.result != null) {

                    if (it.result?.size() == 0) {

                        _progressTextDecider.value = "save"
                        _isProgressDialogOpen.value = true

                        _isUsernameAvailable.value = true

                        val user = Firebase.auth.currentUser

                        if (user != null) {

                            val usernameMap = hashMapOf(
                                "username" to usernameETContent.value,
                                "email" to user.email
                            )

                            Firebase.firestore.collection("users").document(user.uid)
                                .set(usernameMap).addOnCompleteListener { result ->

                                    _progressTextDecider.value = ""
                                    _isProgressDialogOpen.value = false

                                    if (result.isSuccessful) {
                                        _isSaveSuccessful.value = true
                                    } else {

                                        _isSaveSuccessful.value = false

                                        if (result.exception != null) {

                                            if (result.exception!!.localizedMessage != null) {
                                                _errorMessage.value =
                                                    Event(result.exception!!.localizedMessage!!.toString())
                                            } else {
                                                _errorMessage.value = Event("")
                                            }

                                        } else {
                                            _errorMessage.value = Event("")
                                        }

                                    }

                                }

                        } else {
                            _isSaveSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _isUsernameAvailable.value = false
                        _errorMessage.value = Event("")
                    }

                } else {
                    _isUsernameAvailable.value = false
                    _errorMessage.value = Event("")
                }

            } else {

                _isUsernameAvailable.value = false

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


    //########## Is save successful
    private var _isSaveSuccessful = MutableLiveData<Boolean>()
    val isSaveSuccessful: LiveData<Boolean>
        get() = _isSaveSuccessful

}
