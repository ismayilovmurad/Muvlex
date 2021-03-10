package com.martiandeveloper.muvlex.viewmodel.feed

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.check

class EditProfileViewModel:ViewModel() {

    //########## Bio EditText text
    val bioETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Picture ImageView click
    private var _pictureIVClick = MutableLiveData<Event<Boolean>>()
    val pictureIVClick: LiveData<Event<Boolean>>
        get() = _pictureIVClick

    fun onPictureIVClick() {
        _pictureIVClick.value = Event(true)
    }


    //########## Save MaterialButton click
    private var _saveMBTNClick = MutableLiveData<Event<Boolean>>()
    val saveMBTNClick: LiveData<Event<Boolean>>
        get() = _saveMBTNClick

    fun onSaveMBTNClick() {
        _saveMBTNClick.value = Event(true)
    }


    //########## Complete
    private var _complete = MutableLiveData<Boolean>()
    val complete: LiveData<Boolean>
        get() = _complete


    //########## Save
    fun save(imageUri:Uri?){
        if (imageUri != null)
            FirebaseStorage.getInstance().reference.child("user_profile_picture").child(Firebase.auth.currentUser!!.uid)
                .putFile(imageUri).addOnCompleteListener {

                    if (Firebase.auth.currentUser != null) {

                        Firebase.firestore.collection("users")
                            .document(Firebase.auth.currentUser!!.uid)
                            .update(
                                "bio",
                                if (bioETText.value.toString()
                                        .check()
                                ) bioETText.value.toString() else ""
                            ).addOnCompleteListener { result ->
                                _complete.value = true
                            }

                    }

                }
        else
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .update(
                    "bio",
                    if (bioETText.value.toString()
                            .check()
                    ) bioETText.value.toString() else ""
                ).addOnCompleteListener { _ ->
                    _complete.value = true
                }
    }

}
