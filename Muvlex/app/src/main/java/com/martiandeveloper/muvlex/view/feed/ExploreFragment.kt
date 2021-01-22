package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentExploreBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.viewmodel.feed.ExploreViewModel

class ExploreFragment : Fragment() {

    private lateinit var fragmentExploreBinding: FragmentExploreBinding

    private lateinit var exploreViewModel: ExploreViewModel

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

        return fragmentExploreBinding.root

    }

    private fun observe() {

        with(exploreViewModel) {

            searchETClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    navigate(ExploreFragmentDirections.actionExploreFragmentToSearchFragment())
                }

            })

        }

    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

}
