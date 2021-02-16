package com.martiandeveloper.muvlex.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {

    //########## Feed enable
    private var _feedEnable = MutableLiveData<Boolean>()
    val feedEnable: LiveData<Boolean>
        get() = _feedEnable


    //########## Is email verified
    fun isEmailVerified() {
        if (Firebase.auth.currentUser!!.isEmailVerified) hasUserUsername() else _feedEnable.value =
            false
    }


    //########## Has user username
    private fun hasUserUsername() {

        Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get()
            .addOnCompleteListener {
                _feedEnable.value =
                    it.isSuccessful && it.result != null && it.result!!.get("username") != null
            }

    }

}
