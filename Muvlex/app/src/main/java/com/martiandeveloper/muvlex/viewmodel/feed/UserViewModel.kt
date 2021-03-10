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
import com.martiandeveloper.muvlex.repository.UserPostDataSource
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import com.martiandeveloper.muvlex.utils.errorMessageVoid
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

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


    //########## User's posts will appear here LinearLayout gone
    private var _usersPostsWillAppearHereLLGone = MutableLiveData<Boolean>()
    val usersPostsWillAppearHereLLGone: LiveData<Boolean>
        get() = _usersPostsWillAppearHereLLGone

    fun isUsersPostsWillAppearHereLLGone(gone: Boolean) {
        _usersPostsWillAppearHereLLGone.value = gone
    }


    //########## Follow MaterialButton enable
    private var _followMBTNEnable = MutableLiveData<Boolean>()
    val followMBTNEnable: LiveData<Boolean>
        get() = _followMBTNEnable


    //########## Bio MaterialTextView text
    private var _bio = MutableLiveData<String>()
    val bio: LiveData<String>
        get() = _bio

    fun setBio(bio: String) {
        _bio.value = bio
    }


    //########## Follow
    fun follow(uid1: String) {

        _followMBTNEnable.value = false

        with(Firebase.auth.currentUser) {

            if (this != null) {

                Firebase
                    .firestore
                    .collection("users")
                    .document(uid)
                    .update(
                        "following",
                        FieldValue.arrayUnion(Firebase.firestore.document("users/$uid1"))
                    )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            Firebase
                                .firestore
                                .collection("users")
                                .document(uid1)
                                .update(
                                    "followers",
                                    FieldValue.arrayUnion(Firebase.firestore.document("users/$uid"))
                                )
                                .addOnCompleteListener { it1 ->
                                    if (it1.isSuccessful) {
                                        _followMBTNEnable.value = true
                                    } else _errorMessage.value = errorMessageVoid(it1)
                                }

                        } else _errorMessage.value = errorMessageVoid(it)
                    }

            } else _errorMessage.value = Event("")

        }

    }


    //########## Unfollow
    fun unfollow(uid1: String) {

        _followMBTNEnable.value = false

        with(Firebase.auth.currentUser) {

            if (this != null) {

                Firebase
                    .firestore
                    .collection("users")
                    .document(uid)
                    .update(
                        "following",
                        FieldValue.arrayRemove(Firebase.firestore.document("users/$uid1"))
                    )
                    .addOnCompleteListener {

                        if (it.isSuccessful) {

                            Firebase
                                .firestore
                                .collection("users")
                                .document(uid1)
                                .update(
                                    "followers",
                                    FieldValue.arrayRemove(Firebase.firestore.document("users/$uid"))
                                )
                                .addOnCompleteListener { it1 ->
                                    if (it1.isSuccessful) {
                                        _followMBTNEnable.value = true
                                    } else _errorMessage.value = errorMessageVoid(it1)
                                }

                        } else _errorMessage.value = errorMessageVoid(it)

                    }

            } else _errorMessage.value = Event("")

        }

    }


    //########## Get data
    fun getData(uid: String, adapter: UserPostAdapter) {

        val listData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            UserPostDataSource(uid, FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                adapter.submitData(it)
            }

        }

    }

}
