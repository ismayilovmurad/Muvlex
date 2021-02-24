package com.martiandeveloper.muvlex.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.muvlex.utils.Event

class PrivacyPolicyViewModel : ViewModel() {

    //########## Google Play Services MaterialTextView click
    private var _googlePlayServicesMTVClick = MutableLiveData<Event<Boolean>>()
    val googlePlayServicesMTVClick: LiveData<Event<Boolean>>
        get() = _googlePlayServicesMTVClick

    fun onGooglePlayServicesMTVClick() {
        _googlePlayServicesMTVClick.value = Event(true)
    }


    //########## Google Analytics for Firebase MaterialTextView click
    private var _googleAnalyticsForFirebaseMTVClick = MutableLiveData<Event<Boolean>>()
    val googleAnalyticsForFirebaseMTVClick: LiveData<Event<Boolean>>
        get() = _googleAnalyticsForFirebaseMTVClick

    fun onGoogleAnalyticsForFirebaseMTVClick() {
        _googleAnalyticsForFirebaseMTVClick.value = Event(true)
    }


    //########## Firebase Crashlytics MaterialTextView click
    private var _firebaseCrashlyticsMTVClick = MutableLiveData<Event<Boolean>>()
    val firebaseCrashlyticsMTVClick: LiveData<Event<Boolean>>
        get() = _firebaseCrashlyticsMTVClick

    fun onFirebaseCrashlyticsMTVClick() {
        _firebaseCrashlyticsMTVClick.value = Event(true)
    }


    //########## Facebook MaterialTextView click
    private var _facebookMTVClick = MutableLiveData<Event<Boolean>>()
    val facebookMTVClick: LiveData<Event<Boolean>>
        get() = _facebookMTVClick

    fun onFacebookMTVClick() {
        _facebookMTVClick.value = Event(true)
    }


    //########## Muvlex gmail MaterialTextView click
    private var _muvlexGmailMTVClick = MutableLiveData<Event<Boolean>>()
    val muvlexGmailMTVClick: LiveData<Event<Boolean>>
        get() = _muvlexGmailMTVClick

    fun onMuvlexGmailMTVClick() {
        _muvlexGmailMTVClick.value = Event(true)
    }


    //########## Privacy policy template MaterialTextView click
    private var _privacyPolicyTemplateMTVClick = MutableLiveData<Event<Boolean>>()
    val privacyPolicyTemplateMTVClick: LiveData<Event<Boolean>>
        get() = _privacyPolicyTemplateMTVClick

    fun onPrivacyPolicyTemplateMTVClick() {
        _privacyPolicyTemplateMTVClick.value = Event(true)
    }


    //########## App Privacy Policy Generator MaterialTextView click
    private var _appPrivacyPolicyGeneratorMTVClick = MutableLiveData<Event<Boolean>>()
    val appPrivacyPolicyGeneratorMTVClick: LiveData<Event<Boolean>>
        get() = _appPrivacyPolicyGeneratorMTVClick

    fun onAppPrivacyPolicyGeneratorMTVClick() {
        _appPrivacyPolicyGeneratorMTVClick.value = Event(true)
    }

}
