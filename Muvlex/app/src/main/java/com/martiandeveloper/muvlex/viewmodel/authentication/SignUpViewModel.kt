package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.appLanguage

class SignUpViewModel : ViewModel() {

    //########## Progress text
    private var _progressText = MutableLiveData<String>()
    val progressText: LiveData<String>
        get() = _progressText

    fun setProgressText(text: String) {
        _progressText.value = text
    }


    //########## On continue button click
    private var _onContinueButtonClick = MutableLiveData<Event<Boolean>>()
    val onContinueButtonClick: LiveData<Event<Boolean>>
        get() = _onContinueButtonClick

    fun onContinueButtonClick() {
        _onContinueButtonClick.value = Event(true)
    }


    //########## Email EditText content
    val emailETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Password EditText content
    val passwordETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Confirm password EditText content
    val confirmPasswordETContent: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## On next button click
    private var _onNextButtonClick = MutableLiveData<Event<Boolean>>()
    val onNextButtonClick: LiveData<Event<Boolean>>
        get() = _onNextButtonClick

    fun onNextButtonClick() {
        _onNextButtonClick.value = Event(true)
    }


    //########## On log in TextView click
    private var _onLogInTextViewClick = MutableLiveData<Event<Boolean>>()
    val onLogInTextViewClick: LiveData<Event<Boolean>>
        get() = _onLogInTextViewClick

    fun onLogInTextViewClick() {
        _onLogInTextViewClick.value = Event(true)
    }


    //########## Is next button enable
    private var _isNextButtonEnable = MutableLiveData<Boolean>()
    val isNextButtonEnable: LiveData<Boolean>
        get() = _isNextButtonEnable

    fun setNextButtonEnable(enable: Boolean) {
        _isNextButtonEnable.value = enable
    }


    //########## Sign up
    fun signUp() {

        _progressTextDecider.value = "create"
        _isProgressDialogOpen.value = true

        Firebase.auth.createUserWithEmailAndPassword(
            emailETContent.value!!,
            passwordETContent.value!!
        ).addOnCompleteListener {

            _progressTextDecider.value = ""
            _isProgressDialogOpen.value = false

            if (it.isSuccessful) {
                sendVerification()
            } else {

                _isSignUpSuccessful.value = false

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

    private fun sendVerification() {

        _progressTextDecider.value = "verification"
        _isProgressDialogOpen.value = true

        val user = Firebase.auth.currentUser

        if (user != null) {

            if (!user.isEmailVerified) {

                Firebase.auth.setLanguageCode(appLanguage)

                user.sendEmailVerification().addOnCompleteListener {

                    _progressTextDecider.value = ""
                    _isProgressDialogOpen.value = false

                    if (it.isSuccessful) {
                        _isSignUpSuccessful.value = true
                    } else {

                        _isSignUpSuccessful.value = false

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
                _isSignUpSuccessful.value = false
                _errorMessage.value = Event("")
            }

        } else {
            _isSignUpSuccessful.value = false
            _errorMessage.value = Event("")
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


    //########## Is sign up successful
    private var _isSignUpSuccessful = MutableLiveData<Boolean>()
    val isSignUpSuccessful: LiveData<Boolean>
        get() = _isSignUpSuccessful


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Is success dialog open
    private var _isSuccessDialogOpen = MutableLiveData<Boolean>()
    val isSuccessDialogOpen: LiveData<Boolean>
        get() = _isSuccessDialogOpen

    fun setSuccessDialogOpen(open: Boolean) {
        _isSuccessDialogOpen.value = open
    }


    //########## Check email verification
    fun checkEmailVerification() {

        _isSuccessDialogImageGone.value = true
        _isSuccessDialogProgressGone.value = false

        val user = Firebase.auth.currentUser

        if (user != null) {

            user.reload()

            Firebase.auth.signOut()

            Firebase.auth.signInWithEmailAndPassword(
                emailETContent.value!!,
                passwordETContent.value!!
            ).addOnCompleteListener {

                _isSuccessDialogImageGone.value = false
                _isSuccessDialogProgressGone.value = true

                if (it.isSuccessful) {

                    if (it.result != null) {

                        if (it.result!!.user != null) {

                            if (it.result!!.user!!.isEmailVerified) {
                                _isEmailVerificationSuccessful.value = true
                            } else {
                                _isEmailVerificationSuccessful.value = false
                                _errorMessage.value = Event("unverified")
                            }

                        } else {
                            _isEmailVerificationSuccessful.value = false
                            _errorMessage.value = Event("")
                        }

                    } else {
                        _isEmailVerificationSuccessful.value = false
                        _errorMessage.value = Event("")
                    }

                } else {

                    _isEmailVerificationSuccessful.value = false

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
            _isSuccessDialogImageGone.value = false
            _isSuccessDialogProgressGone.value = true

            _isEmailVerificationSuccessful.value = false
            _errorMessage.value = Event("")
        }

    }


    //########## Is email verification successful
    private var _isEmailVerificationSuccessful = MutableLiveData<Boolean>()
    val isEmailVerificationSuccessful: LiveData<Boolean>
        get() = _isEmailVerificationSuccessful


    //########## Is success dialog progress gone
    private var _isSuccessDialogProgressGone = MutableLiveData<Boolean>()
    val isSuccessDialogProgressGone: LiveData<Boolean>
        get() = _isSuccessDialogProgressGone

    fun setIsSuccessDialogProgressGone(gone: Boolean) {
        _isSuccessDialogProgressGone.value = gone
    }


    //########## Is success dialog image gone
    private var _isSuccessDialogImageGone = MutableLiveData<Boolean>()
    val isSuccessDialogImageGone: LiveData<Boolean>
        get() = _isSuccessDialogImageGone

    fun setIsSuccessDialogImageGone(gone: Boolean) {
        _isSuccessDialogImageGone.value = gone
    }


    //########## Is continue button enable
    private var _isContinueButtonEnable = MutableLiveData<Boolean>()
    val isContinueButtonEnable: LiveData<Boolean>
        get() = _isContinueButtonEnable

    fun setIsContinueButtonEnable(enable: Boolean) {
        _isContinueButtonEnable.value = enable
    }

}
