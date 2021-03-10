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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.PostAdapter
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentHomeBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.model.Post
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.viewmodel.feed.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : Fragment(), PostAdapter.ItemClickListener {

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setRecyclerView()

        viewModel.followingList.observe(viewLifecycleOwner, {
            viewModel.getData(adapter)
        })

        Firebase.firestore.collection("posts").document().addSnapshotListener { value, _ ->
            if (value != null && value.exists()) viewModel.getFollowingList()
        }

        return binding.root

    }

    private fun setRecyclerView() {

        adapter = PostAdapter(this)

        binding.fragmentHomeMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        CoroutineScope(Dispatchers.Main).launch {

            adapter.loadStateFlow.collectLatest {

                if (it.refresh is LoadState.NotLoading) if (adapter.itemCount > 0) viewModel.isPostsWillAppearHereLLGone(
                    true
                )

            }

        }

        viewModel.getFollowingList()

    }

    override fun onItemClick(post: Post) {
        val movie = Movie(
            false,
            null,
            post.genreIds,
            post.id,
            post.originalLanguage,
            post.originalTitle,
            post.overview,
            null,
            post.posterPath,
            post.releaseDate,
            post.title,
            false,
            post.voteAverage?.toDouble(),
            null
        )

        startActivity(Intent(context, MovieDetailActivity::class.java).putExtra("movie", movie))
    }

}
