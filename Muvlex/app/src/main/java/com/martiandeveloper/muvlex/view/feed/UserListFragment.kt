package com.martiandeveloper.muvlex.view.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.UserListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentUserListBinding
import com.martiandeveloper.muvlex.model.User
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.utils.showToast
import com.martiandeveloper.muvlex.viewmodel.feed.UserListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListFragment : Fragment(), UserListAdapter.ItemClickListener {

    private lateinit var viewModel: UserListViewModel

    private lateinit var binding: FragmentUserListBinding

    private lateinit var adapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setRecyclerView()

        viewModel.isProgressLLGone(true)

        return binding.root

    }

    private fun observe() {

        searchResult.observe(viewLifecycleOwner, {

            if (networkAvailable) {

                if (!it.isNullOrEmpty()) {

                    with(viewModel) {
                        setProgressMTVText("${getString(R.string.searching_for)} \"${searchResult.value}\" ...")
                        isProgressLLGone(false)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        viewModel.getData(it, adapter)
                    }

                }

            } else R.string.no_internet_connection.showToast(requireContext())

        })

    }

    private fun setRecyclerView() {

        adapter = UserListAdapter(this)

        binding.fragmentUserListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        CoroutineScope(Dispatchers.Main).launch {

            adapter.loadStateFlow.collectLatest {

                if (it.refresh is LoadState.NotLoading) if (adapter.itemCount > 0) viewModel.isSearchResultWillAppearHereMTVGone(
                    true
                )

                with(viewModel) {

                    when (it.append) {

                        is LoadState.Loading -> {
                            setProgressMTVText("${getString(R.string.searching_for)} \"${searchResult.value}\" ...")
                            isProgressLLGone(false)
                        }

                        is LoadState.NotLoading -> {
                            setProgressMTVText("")
                            isProgressLLGone(true)
                        }

                        is LoadState.Error -> {
                            setProgressMTVText("")
                            isProgressLLGone(true)
                            if (!networkAvailable) R.string.no_internet_connection.showToast(
                                requireContext()
                            )
                        }

                    }

                }

            }

        }

    }

    override fun onItemClick(user: User) {

        if (Firebase.auth.currentUser != null) {

            if (user.uid != null) {

                if (Firebase.auth.currentUser!!.uid != user.uid) {
                    startActivity(
                        Intent(context, UserDetailActivity::class.java).putExtra("uid", user.uid)
                            .putExtra("username", user.username)
                    )
                }

            }

        }

    }

}
