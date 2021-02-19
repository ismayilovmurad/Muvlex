package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.HomePostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentHomeBinding
import com.martiandeveloper.muvlex.model.HomePost
import com.martiandeveloper.muvlex.viewmodel.feed.HomeViewModel
import timber.log.Timber


class HomeFragment : Fragment(), HomePostAdapter.ItemClickListener {


    private lateinit var homeViewModel: HomeViewModel

    private lateinit var fragmentHomeItemBinding: FragmentHomeBinding

    private lateinit var homePostAdapter: HomePostAdapter

    private lateinit var friendList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        fragmentHomeItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        fragmentHomeItemBinding.lifecycleOwner = viewLifecycleOwner

        friendList = ArrayList()

        Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnCompleteListener {

            val document: DocumentSnapshot = it.result!!
            val group = document["following"] as List<String>?

            if (group != null) {
                for(i in group){
                    friendList.add(i)
                }
            }



            setUpRecyclerView(friendList)

        }

        return fragmentHomeItemBinding.root

    }

    private fun setUpRecyclerView(friendList: ArrayList<String>) {

        homePostAdapter = HomePostAdapter(this)

        fragmentHomeItemBinding.fragmentHomeMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = homePostAdapter
            it.setHasFixedSize(true)
        }

        homeViewModel.getData(homePostAdapter, friendList)

    }

    override fun onItemClick(homePost: HomePost) {
        TODO("Not yet implemented")
    }

}

