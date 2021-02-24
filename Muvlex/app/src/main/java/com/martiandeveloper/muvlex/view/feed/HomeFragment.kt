package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.HomePostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentHomeBinding
import com.martiandeveloper.muvlex.model.HomePost
import com.martiandeveloper.muvlex.viewmodel.feed.HomeViewModel
import timber.log.Timber

class HomeFragment : Fragment(), HomePostAdapter.ItemClickListener {

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: HomePostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        observe()

        viewModel.getFollowingList()

        return binding.root

    }

    private fun observe() {

        viewModel.followingList.observe(viewLifecycleOwner, {
            setUpRecyclerView()
        })

    }

    private fun setUpRecyclerView() {

        adapter = HomePostAdapter(this)

        binding.fragmentHomeMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
            it.setHasFixedSize(true)
        }

        viewModel.getData(adapter)

    }

    override fun onItemClick(homePost: HomePost) {
        Timber.d(homePost.item_id)
    }

}
