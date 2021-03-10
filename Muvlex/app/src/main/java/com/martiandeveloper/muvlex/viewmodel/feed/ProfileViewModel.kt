package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.model.Language
import com.martiandeveloper.muvlex.repository.UserPostDataSource
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    //########## Edit MaterialButton click
    private var _editMBTNClick = MutableLiveData<Event<Boolean>>()
    val editMBTNClick: LiveData<Event<Boolean>>
        get() = _editMBTNClick

    fun onEditMBTNClick() {
        _editMBTNClick.value = Event(true)
    }


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
    private var _yourPostsWillAppearHereLLGone = MutableLiveData<Boolean>()
    val yourPostsWillAppearHereLLGone: LiveData<Boolean>
        get() = _yourPostsWillAppearHereLLGone

    fun isYourPostsWillAppearHereLLGone(gone: Boolean) {
        _yourPostsWillAppearHereLLGone.value = gone
    }


    //########## Bio MaterialTextView text
    private var _bio = MutableLiveData<String>()
    val bio: LiveData<String>
        get() = _bio

    fun setBio(bio: String) {
        _bio.value = bio
    }


    //########## Main MaterialToolbar title
    private var _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String) {
        _title.value = title
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


    //########## Search EditText text
    val searchETText: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    //########## Language list
    private var _languageList = MutableLiveData<ArrayList<Language>>()
    val languageList: LiveData<ArrayList<Language>>
        get() = _languageList

    fun fillLanguageList(list: ArrayList<Language>) {
        _languageList.value = list
    }


    init {
        _languageList.value = ArrayList()
    }

}
