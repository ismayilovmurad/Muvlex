package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.CHECK_USERNAME
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.USERNAME_NOT_AVAILABLE
import com.martiandeveloper.muvlex.utils.errorMessageVoid

class SignUpUsernameViewModel : ViewModel() {

    //########## Progress MaterialTextView text
    private var _progressMTVText = MutableLiveData<String>()
    val progressMTVText: LiveData<String>
        get() = _progressMTVText

    fun setProgressMTVText(text: String) {
        _progressMTVText.value = text
    }


    //########## Username EditText text
    val usernameETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


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


    //########## Save successful
    private var _saveSuccessful = MutableLiveData<Boolean>()
    val saveSuccessful: LiveData<Boolean>
        get() = _saveSuccessful


    //########## Username error MaterialTextView text
    private var _usernameErrorMTVText = MutableLiveData<String>()
    val usernameErrorMTVText: LiveData<String>
        get() = _usernameErrorMTVText

    fun setUsernameErrorMTVText(text: String) {
        _usernameErrorMTVText.value = text
    }


    //########## Username ProgressBar gone
    private var _usernamePBGone = MutableLiveData<Boolean>()
    val usernamePBGone: LiveData<Boolean>
        get() = _usernamePBGone


    //########## Username ProgressBar gone
    private var _usernameAvailable = MutableLiveData<Boolean>()
    val usernameAvailable: LiveData<Boolean>
        get() = _usernameAvailable


    //########## Privacy policy MaterialTextView click
    private var _privacyPolicyMTVClick = MutableLiveData<Event<Boolean>>()
    val privacyPolicyMTVClick: LiveData<Event<Boolean>>
        get() = _privacyPolicyMTVClick

    fun onPrivacyPolicyMTVClick() {
        _privacyPolicyMTVClick.value = Event(true)
    }


    //########## Is username available
    fun isUsernameAvailable() {

        _nextMBTNEnable.value = false
        _usernamePBGone.value = false

        Firebase.firestore.collection("users").whereEqualTo("username", usernameETText.value).get()
            .addOnCompleteListener {

                _usernamePBGone.value = true

                if (it.isSuccessful) {

                    for (i in it.result!!) {
                        if (i.getString("username") == usernameETText.value) _usernameAvailable.value =
                            false
                    }

                    _usernameAvailable.value =
                        it.result?.size() == 0 && !usernameETText.value.isNullOrEmpty()

                } else _usernameAvailable.value = false

            }

    }


    //########## Save username and email
    fun saveUsernameAndEmail() {

        _progressMTVTextDecider.value = CHECK_USERNAME
        _progressADOpen.value = true

        Firebase.firestore.collection("users").whereEqualTo("username", usernameETText.value).get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    for (i in it.result!!) {

                        if (i.getString("username") == usernameETText.value) {
                            _progressMTVTextDecider.value = ""
                            _progressADOpen.value = false

                            _errorMessage.value = Event(USERNAME_NOT_AVAILABLE)
                        }

                    }

                    if (it.result?.size() == 0 && !usernameETText.value.isNullOrEmpty()) {

                        with(Firebase.auth.currentUser) {

                            if (this != null) {

                                val map = hashMapOf(
                                    "bio" to "",
                                    "email" to email,
                                    "followers" to arrayListOf<String>(),
                                    "following" to arrayListOf<String>(),
                                    "picture" to "",
                                    "uid" to uid,
                                    "username" to usernameETText.value,
                                )

                                Firebase.firestore.collection("users").document(uid)
                                    .set(map).addOnCompleteListener { result ->
                                        _progressMTVTextDecider.value = ""
                                        _progressADOpen.value = false

                                        if (result.isSuccessful) _saveSuccessful.value =
                                            true else _errorMessage.value = errorMessageVoid(result)
                                    }

                            } else _errorMessage.value = Event("")

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


    init {
        _nextMBTNEnable.value = false
        _usernamePBGone.value = true
    }

}
