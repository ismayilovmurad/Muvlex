package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event

class SignUpUsernameViewModel : ViewModel() {

    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Username error MaterialTextView text
    private var _usernameErrorMTVText = MutableLiveData<String>()
    val usernameErrorMTVText: LiveData<String>
        get() = _usernameErrorMTVText

    fun setUsernameErrorMTVText(text: String) {
        _usernameErrorMTVText.value = text
    }


    //########## Username error MaterialTextView gone
    private var _usernameErrorMTVGone = MutableLiveData<Boolean>()
    val usernameErrorMTVGone: LiveData<Boolean>
        get() = _usernameErrorMTVGone

    fun isUsernameErrorMTVGone(gone: Boolean) {
        _usernameErrorMTVGone.value = gone
    }


    //########## Username EditText text
    val usernameETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Username ProgressBar gone
    private var _usernamePBGone = MutableLiveData<Boolean>()
    val usernamePBGone: LiveData<Boolean>
        get() = _usernamePBGone

    fun isUsernamePBGone(gone: Boolean) {
        _usernamePBGone.value = gone
    }


    //########## Next MaterialButton click
    private var _nextMBTNClick = MutableLiveData<Event<Boolean>>()
    val nextMBTNClick: LiveData<Event<Boolean>>
        get() = _nextMBTNClick

    fun onNextMBTNClick() {
        _nextMBTNClick.value = Event(true)
    }


    //########## Privacy policy MaterialTextView click
    private var _privacyPolicyMTVClick = MutableLiveData<Event<Boolean>>()
    val privacyPolicyMTVClick: LiveData<Event<Boolean>>
        get() = _privacyPolicyMTVClick

    fun onPrivacyPolicyMTVClick() {
        _privacyPolicyMTVClick.value = Event(true)
    }


    //########## Next MaterialButton enable
    private var _nextMBTNEnable = MutableLiveData<Boolean>()
    val nextMBTNEnable: LiveData<Boolean>
        get() = _nextMBTNEnable

    fun isNextMBTNEnable(enable: Boolean) {
        _nextMBTNEnable.value = enable
    }


    //########## Is username available
    fun isUsernameAvailable() {

        _usernamePBGone.value = false

        val query =
            Firebase.firestore.collection("users").whereEqualTo("username", usernameETText.value)

        query.get().addOnCompleteListener {

            _usernamePBGone.value = true

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == usernameETText.value) {
                        _usernameAvailable.value = false
                    }

                }

                if (it.result?.size() == 0) {
                    _usernameAvailable.value = true
                }

            } else {

                _usernameAvailable.value = false

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


    //########## Username available
    private var _usernameAvailable = MutableLiveData<Boolean>()
    val usernameAvailable: LiveData<Boolean>
        get() = _usernameAvailable


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Save username and email
    fun saveUsernameAndEmail() {

        _progressMTVTextDecider.value = "check"
        _progressADOpen.value = true

        val query =
            Firebase.firestore.collection("users").whereEqualTo("username", usernameETText.value)

        query.get().addOnCompleteListener {

            _progressMTVTextDecider.value = ""
            _progressADOpen.value = false

            if (it.isSuccessful) {

                for (i in it.result!!) {

                    if (i.getString("username") == usernameETText.value) {
                        _usernameAvailable.value = false
                    }

                }

                if (it.result != null) {

                    if (it.result?.size() == 0) {

                        _progressMTVTextDecider.value = "save"
                        _progressADOpen.value = true

                        _usernameAvailable.value = true

                        val user = Firebase.auth.currentUser

                        if (user != null) {

                            val usernameMap = hashMapOf(
                                "username" to usernameETText.value,
                                "email" to user.email
                            )

                            Firebase.firestore.collection("users").document(user.uid)
                                .set(usernameMap).addOnCompleteListener { result ->

                                    _progressMTVTextDecider.value = ""
                                    _progressADOpen.value = false

                                    if (result.isSuccessful) {
                                        _saveSuccessful.value = true
                                    } else {

                                        _saveSuccessful.value = false

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
                            _saveSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _usernameAvailable.value = false
                        _errorMessage.value = Event("")
                    }

                } else {
                    _usernameAvailable.value = false
                    _errorMessage.value = Event("")
                }

            } else {

                _usernameAvailable.value = false

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


    //########## Progress MaterialTextView text decider
    private var _progressMTVTextDecider = MutableLiveData<String>()
    val progressMTVTextDecider: LiveData<String>
        get() = _progressMTVTextDecider


    //########## Progress AlertDialog open
    private var _progressADOpen = MutableLiveData<Boolean>()
    val progressADOpen: LiveData<Boolean>
        get() = _progressADOpen


    //########## Save successful
    private var _saveSuccessful = MutableLiveData<Boolean>()
    val saveSuccessful: LiveData<Boolean>
        get() = _saveSuccessful

}
