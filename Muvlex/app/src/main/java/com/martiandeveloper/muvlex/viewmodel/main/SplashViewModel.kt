package com.martiandeveloper.muvlex.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {

    //########## Feed enable
    private var _feedEnable = MutableLiveData<Boolean>()
    val feedEnable: LiveData<Boolean>
        get() = _feedEnable


    //########## Is email verified
    fun isEmailVerified(user: FirebaseUser) {

        if (user.isEmailVerified) {
            hasUserUsername(user)
        } else {
            Firebase.auth.signOut()
            _feedEnable.value = false
        }

    }


    //########## Has user username
    private fun hasUserUsername(user: FirebaseUser) {

        Firebase.firestore.collection("users").document(user.uid).get().addOnCompleteListener {

            if (it.isSuccessful) {

                if (it.result != null) {

                    if (it.result!!.get("username") != null) {
                        _feedEnable.value = true
                    } else {
                        Firebase.auth.signOut()
                        _feedEnable.value = false
                    }

                } else {
                    Firebase.auth.signOut()
                    _feedEnable.value = false
                }

            } else {
                Firebase.auth.signOut()
                _feedEnable.value = false
            }

        }

    }

}
