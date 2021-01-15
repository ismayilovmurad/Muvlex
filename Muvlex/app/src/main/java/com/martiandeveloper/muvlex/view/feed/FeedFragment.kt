package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.martiandeveloper.muvlex.R

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        NavigationUI.setupWithNavController(
            view.findViewById<BottomNavigationView>(R.id.fragment_feed_mainBNV),
            (childFragmentManager.findFragmentById(R.id.fragment_feed_feedFCV) as NavHostFragment).navController
        )

        return view
    }

}
