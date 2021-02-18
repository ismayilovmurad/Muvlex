package com.martiandeveloper.muvlex.viewmodel.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.view.main.SplashFragmentDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    //########## Feed enable
    private var _feedEnable = MutableLiveData<Boolean>()
    val feedEnable: LiveData<Boolean>
        get() = _feedEnable


    //########## Decide where to go
    fun decideWhereToGo(view: View) {

        viewModelScope.launch {
            delay(2000)
            if (Firebase.auth.currentUser != null)
                if (networkAvailable) isEmailVerified() else view.navigate(
                    SplashFragmentDirections.actionSplashFragmentToLogInFragment()
                )
            else view.navigate(SplashFragmentDirections.actionSplashFragmentToLogInFragment())
        }

    }


    //########## Is email verified
    private fun isEmailVerified() {
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
