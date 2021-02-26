package com.martiandeveloper.muvlex.view.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentSearchBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.SearchViewModel

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        openKeyboardForSearchET = true

        searchResult.value = ""

        binding.fragmentSearchSearchMTV.setToggle(requireContext())

        setRemoveAllListenerToSearchET()

        setDoneListenerToSearchET()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            val viewPager = fragmentSearchMainVP
            viewPager.adapter = ScreenSlidePagerAdapter()

            TabLayoutMediator(fragmentSearchMainTL, viewPager) { tab, position ->

                tab.text = getString(

                    when (position) {
                        0 -> R.string.movies
                        1 -> R.string.series
                        else -> R.string.users
                    }

                )

            }.attach()

            if (openKeyboardForSearchET) {
                fragmentSearchSearchET.requestFocus()
                requireContext().showKeyboard(fragmentSearchSearchET)
            }

        }

    }

    private fun observe() {

        with(viewModel) {

            searchETText.observe(viewLifecycleOwner, {

                with(binding.fragmentSearchSearchET) {
                    if (it.isNullOrEmpty()) setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    ) else setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
                }

            })

            backIVClick.observe(viewLifecycleOwner, EventObserver {
                activity?.hideKeyboard()
                findNavController().navigateUp()
            })

            searchMTVClick.observe(viewLifecycleOwner, EventObserver {

                activity?.hideKeyboard()

                with(searchETText.value) {
                    if (!isNullOrEmpty()) searchResult.value = this
                }

            })

        }

    }

    private inner class ScreenSlidePagerAdapter :
        FragmentStateAdapter(this@SearchFragment) {

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

        with(binding.fragmentSearchSearchET) {

            setOnTouchListener(View.OnTouchListener { _, event ->

                if (event.action == MotionEvent.ACTION_UP)
                    if (compoundDrawables[2] != null)

                        if (event.rawX >= right - compoundDrawables[2].bounds.width()
                        ) {
                            text.clear()
                            return@OnTouchListener true
                        }

                false

            })

        }

    }

    private fun setDoneListenerToSearchET() {

        binding.fragmentSearchSearchET.setOnEditorActionListener(
            OnEditorActionListener { _, actionId, event ->

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN
                            && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {

                    activity?.hideKeyboard()

                    with(viewModel.searchETText.value) {
                        if (!isNullOrEmpty()) searchResult.value = this
                    }

                    return@OnEditorActionListener true

                }

                false

            })

    }

}
