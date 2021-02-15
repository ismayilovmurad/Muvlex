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
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentUserListBinding
import com.martiandeveloper.muvlex.model.User
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.UserListViewModel

class UserListFragment : Fragment(), UserPostAdapter.ItemClickListener {

    private lateinit var userListViewModel: UserListViewModel

    private lateinit var fragmentUserListBinding: FragmentUserListBinding

    private lateinit var userListAdapter: UserPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userListViewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)

        fragmentUserListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)

        fragmentUserListBinding.let {
            it.userListViewModel = userListViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(userListViewModel) {
            isNoInternetConnectionMTVGone(true)
            isSearchingForLLGone(true)
            isSearchingForLL2Gone(true)
            isNoResultsFoundForMTVGone(true)
        }

        setUpRecyclerView()

        observe()

        return fragmentUserListBinding.root

    }

    private fun observe() {

        with(userListViewModel) {

            searchResult.observe(viewLifecycleOwner, {

                if (!it.isNullOrEmpty()) {

                    isNoResultsFoundForMTVGone(true)

                    if (networkAvailable) {
                        isNoInternetConnectionMTVGone(true)
                        isMovieRVGone(false)
                        setSearchingForMTVText("${getString(R.string.searching_for)} \"$it\"...")
                        isSearchingForLLGone(false)
                        userListViewModel.getData(userListAdapter)
                    } else {
                        isNoInternetConnectionMTVGone(false)
                        isMovieRVGone(true)
                        isSearchingForLLGone(true)
                    }

                } else isMovieRVGone(true)

            })

        }

    }

    private fun setUpRecyclerView() {

        userListAdapter = UserPostAdapter(this)

        fragmentUserListBinding.fragmentUserListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = userListAdapter
            it.setHasFixedSize(true)
        }



    }

    override fun onItemClick(user: User) {

        view.navigate(SearchFragmentDirections.actionSearchFragmentToUserFragment(user.user_id!!))

    }

}
