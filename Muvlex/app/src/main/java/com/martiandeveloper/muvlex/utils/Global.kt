package com.martiandeveloper.muvlex.utils

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

var networkAvailable = false
var appLanguage = "en"
var searchResult: MutableLiveData<String> = MutableLiveData()
var openKeyboardForSearchET = false

fun errorMessageDocument(task: Task<DocumentSnapshot>): Event<String> {
    return if (task.exception != null) if (task.exception!!.localizedMessage != null) Event(
        task.exception!!.localizedMessage!!.toString()
    ) else Event("") else Event("")
}

fun errorMessageQuery(task: Task<QuerySnapshot>): Event<String> {
    return if (task.exception != null) if (task.exception!!.localizedMessage != null) Event(
        task.exception!!.localizedMessage!!.toString()
    ) else Event("") else Event("")
}

fun errorMessageVoid(task: Task<Void>): Event<String> {
    return if (task.exception != null) if (task.exception!!.localizedMessage != null) Event(
        task.exception!!.localizedMessage!!.toString()
    ) else Event("") else Event("")
}

fun errorMessageAuth(task: Task<AuthResult>): Event<String> {
    return if (task.exception != null) if (task.exception!!.localizedMessage != null) Event(
        task.exception!!.localizedMessage!!.toString()
    ) else Event("") else Event("")
}
