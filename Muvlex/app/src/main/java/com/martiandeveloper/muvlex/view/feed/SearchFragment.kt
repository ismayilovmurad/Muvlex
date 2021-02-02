package com.martiandeveloper.muvlex.view.feed

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentSearchBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var fragmentSearchBinding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        fragmentSearchBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        fragmentSearchBinding.let {
            it.searchViewModel = searchViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setRemoveAllListenerToSearchET()

        return fragmentSearchBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val viewPager = fragmentSearchBinding.fragmentSearchMainVP
        viewPager.adapter = ScreenSlidePagerAdapter(this)

        TabLayoutMediator(fragmentSearchBinding.fragmentSearchMainTL, viewPager) { tab, position ->

            tab.text = when (position) {
                0 -> getString(R.string.movies)
                1 -> getString(R.string.series)
                else -> getString(R.string.users)
            }

        }.attach()

        if (openKeyboardForSearchET) {
            val fragmentSearchSearchET = fragmentSearchBinding.fragmentSearchSearchET
            fragmentSearchSearchET.requestFocus()

            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                fragmentSearchSearchET,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

    }

    private fun observe() {

        with(searchViewModel) {

            searchETText.observe(viewLifecycleOwner, {

                with(fragmentSearchBinding.fragmentSearchSearchET) {

                    viewLifecycleOwner.lifecycleScope.launch {

                        if (it.isNullOrEmpty()) {
                            setCompoundDrawables(0)
                            searchResult.value = ""
                        } else {
                            setCompoundDrawables(R.drawable.ic_close)
                            searchResult.value = it
                        }

                    }

                }

            })

            backIVClick.observe(viewLifecycleOwner, EventObserver {
                if (it) findNavController().navigateUp()
            })

        }

    }

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = VIEWPAGER_PAGES

        override fun createFragment(position: Int): Fragment {

            return when (position) {
                0 -> MovieListFragment()
                1 -> SeriesListFragment()
                else -> UserListFragment()
            }

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setRemoveAllListenerToSearchET() {

        fragmentSearchBinding.fragmentSearchSearchET.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP)
                if (fragmentSearchBinding.fragmentSearchSearchET.compoundDrawables[2] != null)

                    if (event.rawX >= fragmentSearchBinding.fragmentSearchSearchET.right - fragmentSearchBinding.fragmentSearchSearchET.compoundDrawables[2].bounds.width()
                    ) {
                        searchResult.value = ""
                        fragmentSearchBinding.fragmentSearchSearchET.text.clear()

                        return@OnTouchListener true
                    }

            false

        })

    }

}
