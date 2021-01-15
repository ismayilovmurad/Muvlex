package com.martiandeveloper.muvlex.view.feed

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentSearchBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.viewmodel.feed.SearchViewModel

class SearchFragment : Fragment() {

    private lateinit var fragmentSearchBinding: FragmentSearchBinding

    private lateinit var searchViewModel: SearchViewModel

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

        return fragmentSearchBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentSearchSearchET = fragmentSearchBinding.fragmentSearchSearchET
        fragmentSearchSearchET.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(fragmentSearchSearchET, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun observe() {

        with(searchViewModel) {

            searchETContent.observe(viewLifecycleOwner, {

                if (!it.isNullOrEmpty()) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }

            })

            onBackImageViewClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    navigate(SearchFragmentDirections.actionSearchFragmentToExploreFragment())
                }

            })

        }

    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

}
