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
import com.martiandeveloper.muvlex.adapter.HomePostAdapter
import com.martiandeveloper.muvlex.repository.HomePostSource
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

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
    fun getData(homePostAdapter: HomePostAdapter) {

        val listData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            HomePostSource(FirebaseFirestore.getInstance(), _followingList.value!!)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {

            listData.collect {
                homePostAdapter.submitData(it)
            }

        }

    }


    init {
        _followingList.value = ArrayList()
    }

}
