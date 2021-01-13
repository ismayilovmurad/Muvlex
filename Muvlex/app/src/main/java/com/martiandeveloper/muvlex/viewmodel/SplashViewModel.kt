package com.martiandeveloper.muvlex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {

    //########## Is feed enable
    private var _isFeedEnable = MutableLiveData<Boolean>()
    val isFeedEnable: LiveData<Boolean>
        get() = _isFeedEnable


    //########## Check email verification
    fun checkEmailVerification(user: FirebaseUser) {

        if (user.isEmailVerified) {
            checkIfUserHasUsername(user)
        } else {
            Firebase.auth.signOut()
            _isFeedEnable.value = false
        }

    }


    //########## Check if the user has username
    private fun checkIfUserHasUsername(user: FirebaseUser) {

        Firebase.firestore.collection("users").document(user.uid).get().addOnCompleteListener {

            if (it.isSuccessful) {

                if (it.result != null) {

                    if (it.result!!.get("username") != null) {
                        _isFeedEnable.value = true
                    } else {
                        Firebase.auth.signOut()
                        _isFeedEnable.value = false
                    }

                } else {
                    Firebase.auth.signOut()
                    _isFeedEnable.value = false
                }

            } else {
                Firebase.auth.signOut()
                _isFeedEnable.value = false
            }

        }

    }

}
