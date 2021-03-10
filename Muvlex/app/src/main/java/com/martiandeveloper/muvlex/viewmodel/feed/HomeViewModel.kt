package com.martiandeveloper.muvlex.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.adapter.PostAdapter
import com.martiandeveloper.muvlex.repository.ExplorePostDataSource
import com.martiandeveloper.muvlex.repository.HomePostDataSource
import com.martiandeveloper.muvlex.utils.Event
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    //########## Posts will appear here LinearLayout gone
    private var _postsWillAppearHereLLGone = MutableLiveData<Boolean>()
    val postsWillAppearHereLLGone: LiveData<Boolean>
        get() = _postsWillAppearHereLLGone

    fun isPostsWillAppearHereLLGone(gone: Boolean) {
        _postsWillAppearHereLLGone.value = gone
    }


    //########## Following list
    private var _followingList = MutableLiveData<ArrayList<Any>>()
    val followingList: LiveData<ArrayList<Any>>
        get() = _followingList


    //########## Get following list
    fun getFollowingList() {

        Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get()
            .addOnCompleteListener {

                val list = ArrayList<Any>()

                if (it.result != null)

                    with(it.result!!["following"] as ArrayList<*>?) {

                        if (this != null) {

                            for (i in this) {
                                if (i != null) list.add(i)
                            }

                            _followingList.value = list

                        }

                    }

            }

    }


    //########## Get data
    fun getData(adapter: PostAdapter) {

        val listData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            HomePostDataSource(_followingList.value!!, FirebaseFirestore.getInstance())
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                adapter.submitData(it)
            }

        }

    }

    init {
        _followingList.value = ArrayList()
    }

}
