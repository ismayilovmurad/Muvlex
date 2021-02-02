package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private lateinit var fragmentFeedBinding: FragmentFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        fragmentFeedBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)

        NavigationUI.setupWithNavController(
            fragmentFeedBinding.fragmentFeedMainBNV,
            (childFragmentManager.findFragmentById(R.id.fragment_feed_mainFCV) as NavHostFragment).navController
        )

        return fragmentFeedBinding.root
    }

}
