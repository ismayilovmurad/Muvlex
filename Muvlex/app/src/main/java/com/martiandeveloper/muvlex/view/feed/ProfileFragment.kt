package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.ProfilePostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentProfileBinding
import com.martiandeveloper.muvlex.model.ProfilePost
import com.martiandeveloper.muvlex.viewmodel.feed.ProfileViewModel

class ProfileFragment : Fragment(), ProfilePostAdapter.ItemClickListener {

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var fragmentProfileItemBinding: FragmentProfileBinding

    private lateinit var profilePostAdapter: ProfilePostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        fragmentProfileItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        fragmentProfileItemBinding.lifecycleOwner = viewLifecycleOwner

        setUpRecyclerView()

        return fragmentProfileItemBinding.root
    }

    private fun setUpRecyclerView() {

        profilePostAdapter = ProfilePostAdapter(this)

        fragmentProfileItemBinding.fragmentProfilePostRV.let {
            it.adapter = profilePostAdapter
            it.layoutManager = LinearLayoutManager(context)
            it.setHasFixedSize(true)
        }

        profileViewModel.getData(profilePostAdapter)

    }

    override fun onItemClick(profilePost: ProfilePost) {
        Toast.makeText(context, profilePost.star, Toast.LENGTH_SHORT).show()
    }

}
