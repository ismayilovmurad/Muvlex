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
import com.martiandeveloper.muvlex.adapter.UserListAdapter
import com.martiandeveloper.muvlex.databinding.FragmentUserListBinding
import com.martiandeveloper.muvlex.model.User
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.utils.searchResult
import com.martiandeveloper.muvlex.viewmodel.feed.UserListViewModel

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

        observe()

        setRecyclerView()

        return binding.root
    }

    private fun observe() {

        searchResult.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) viewModel.getData(it, adapter)
        })

    }

    private fun setRecyclerView() {

        adapter = UserListAdapter(this)

        binding.fragmentUserListMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

    }

    override fun onItemClick(user: User) {
        view.navigate(SearchFragmentDirections.actionSearchFragmentToUserFragment(user))
    }

}
