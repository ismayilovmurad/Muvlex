package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.utils.searchResult

class MovieListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)

        searchResult.observe(viewLifecycleOwner, {

            view.findViewById<TextView>(R.id.fragment_movie_list_mainTV).text = it

        })

        return view

    }

}
