package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.repository.UserPostSource
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.errorMessageVoid
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel : ViewModel() {

    //########## Main MaterialToolbar title
    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    fun setUsername(username: String) {
        _username.value = username
    }


    //########## Follow MaterialButton click
    private var _followMBTNClick = MutableLiveData<Event<Boolean>>()
    val followMBTNClick: LiveData<Event<Boolean>>
        get() = _followMBTNClick

    fun onFollowMBTNClick() {
        _followMBTNClick.value = Event(true)
    }


    //########## Follow MaterialButton text
    private var _followMBTNText = MutableLiveData<String>()
    val followMBTNText: LiveData<String>
        get() = _followMBTNText

    fun setFollowMBTNText(text: String) {
        _followMBTNText.value = text
    }


    //########## Follow successful
    private var _followSuccessful = MutableLiveData<Boolean>()
    val followSuccessful: LiveData<Boolean>
        get() = _followSuccessful


    //########## Error message
    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage


    //########## Posts MaterialTextView text
    private var _posts = MutableLiveData<String>()
    val posts: LiveData<String>
        get() = _posts

    fun setPosts(posts: String) {
        _posts.value = posts
    }


    //########## Following MaterialTextView text
    private var _following = MutableLiveData<String>()
    val following: LiveData<String>
        get() = _following

    fun setFollowing(following: String) {
        _following.value = following
    }


    //########## Followers MaterialTextView text
    private var _followers = MutableLiveData<String>()
    val followers: LiveData<String>
        get() = _followers

    fun setFollowers(followers: String) {
        _followers.value = followers
    }


    //########## Follow
    fun follow(uid: String) {

        with(Firebase.auth.currentUser) {

            if (this != null) {

                Firebase
                    .firestore
                    .collection("users")
                    .document(Firebase.auth.currentUser!!.uid)
                    .update("following", FieldValue.arrayUnion(uid))
                    .addOnCompleteListener {
                        if (it.isSuccessful) _followSuccessful.value =
                            true else _errorMessage.value = errorMessageVoid(it)
                    }

            } else _errorMessage.value = Event("")

        }

    }


    //########## Get data
    fun getData(userPostAdapter: UserPostAdapter,uid: String) {
        Timber.d("wE'RE HERE ")

        val listData = Pager(PagingConfig(pageSize = 20)) {
            UserPostSource(FirebaseFirestore.getInstance(),uid)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                Timber.d("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY "+it.toString())
                userPostAdapter.submitData(it)
            }

        }

    }

}
