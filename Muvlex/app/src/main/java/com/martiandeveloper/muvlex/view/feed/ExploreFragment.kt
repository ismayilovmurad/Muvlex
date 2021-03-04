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
import com.martiandeveloper.muvlex.adapter.ExplorePostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentExploreBinding
import com.martiandeveloper.muvlex.model.ExplorePost
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.viewmodel.feed.ExploreViewModel

class ExploreFragment : Fragment(), ExplorePostAdapter.ItemClickListener {

    private lateinit var exploreViewModel: ExploreViewModel

    private lateinit var fragmentExploreBinding: FragmentExploreBinding

    private lateinit var explorePostAdapter: ExplorePostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        exploreViewModel = ViewModelProviders.of(this).get(ExploreViewModel::class.java)

        fragmentExploreBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false)

        fragmentExploreBinding.let {
            it.exploreViewModel = exploreViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setUpRecyclerView()

        return fragmentExploreBinding.root

    }

    private fun observe() {

        exploreViewModel.searchETClick.observe(viewLifecycleOwner, EventObserver {
            if (it) view.navigate(ExploreFragmentDirections.actionExploreFragmentToSearchFragment())
        })

    }

    private fun setUpRecyclerView() {

        explorePostAdapter = ExplorePostAdapter(this)

        fragmentExploreBinding.fragmentExploreMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = explorePostAdapter
            it.setHasFixedSize(true)
        }

        exploreViewModel.getData(explorePostAdapter)

    }

    override fun onItemClick(profilePost: ExplorePost) {
        Toast.makeText(context, profilePost.rating, Toast.LENGTH_SHORT).show()
    }

}
