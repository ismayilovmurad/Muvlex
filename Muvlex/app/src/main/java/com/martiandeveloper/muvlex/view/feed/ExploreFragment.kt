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
import com.martiandeveloper.muvlex.databinding.FragmentExploreBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.model.Post
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.viewmodel.feed.ExploreViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExploreFragment : Fragment(), PostAdapter.ItemClickListener {

    private lateinit var viewModel: ExploreViewModel

    private lateinit var binding: FragmentExploreBinding

    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(ExploreViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        viewModel.searchETClick.observe(viewLifecycleOwner, EventObserver {
            view.navigate(ExploreFragmentDirections.actionExploreFragmentToSearchFragment())
        })

        setRecyclerView()

        Firebase.firestore.collection("posts").document().addSnapshotListener { value, _ ->
            if (value != null && value.exists()) viewModel.getData(adapter)
        }

        return binding.root

    }

    private fun setRecyclerView() {

        adapter = PostAdapter(this)

        binding.fragmentExploreMainRV.let {
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

        viewModel.getData(adapter)

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
