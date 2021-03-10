package com.martiandeveloper.muvlex.view.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentUserBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.model.Post
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserFragment : Fragment(), UserPostAdapter.ItemClickListener {

    private lateinit var viewModel: UserViewModel

    private lateinit var binding: FragmentUserBinding

    private lateinit var adapter: UserPostAdapter

    private val args: UserFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setViewData()

        setRecyclerView()

        return binding.root

    }

    private fun observe() {

        with(viewModel) {

            followMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.fragmentUserFollowMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            followMBTNClick.observe(viewLifecycleOwner, {

                with(args.uid) {

                    if (networkAvailable) {

                        if (this.check()) {
                            if (binding.fragmentUserFollowMBTN.text == getString(R.string.follow)) follow(
                                this
                            ) else unfollow(this)
                        }

                    } else context?.let { it1 -> R.string.no_internet_connection.showToast(it1) }

                }

            })

            errorMessage.observe(viewLifecycleOwner, {
                context?.let { it1 -> R.string.something_went_wrong_try_again_later.showToast(it1) }
            })

        }

    }

    private fun setViewData() {

        if (args.uid.check())

            Firebase.firestore.collection("users").document(args.uid)
                .addSnapshotListener { value, _ ->

                    if (value != null && value.exists()) {

                        context?.let {

                            FirebaseStorage.getInstance().reference.child("user_profile_picture").child(args.uid).downloadUrl.addOnSuccessListener {
                                binding.fragmentUserPosterIV.loadWithUri(requireContext(),it)
                            }.addOnFailureListener {
                                binding.fragmentUserPosterIV.load(requireContext(), null)
                            }

                        }

                        viewModel.setFollowing((value.get("following") as ArrayList<*>).size.toString())
                        viewModel.setFollowers((value.get("followers") as ArrayList<*>).size.toString())
                        viewModel.setBio(value.get("bio").toString())

                        context?.let {
                            viewModel.setFollowMBTNText(
                                if ((value.get("followers") as ArrayList<*>).contains(
                                        Firebase.firestore.document(
                                            "users/${Firebase.auth.currentUser!!.uid}"
                                        )
                                    )
                                ) getString(
                                    R.string.unfollow
                                ) else getString(R.string.follow)
                            )
                        }

                    }

                }

    }

    private fun setRecyclerView() {

        adapter = UserPostAdapter(this)

        binding.fragmentUserMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        CoroutineScope(Dispatchers.Main).launch {

            adapter.loadStateFlow.collectLatest {

                viewModel.setPosts(adapter.itemCount.toString())

                if (it.refresh is LoadState.NotLoading) if (adapter.itemCount > 0) viewModel.isUsersPostsWillAppearHereLLGone(
                    true
                )

            }

        }

        if (args.uid.check()) viewModel.getData(args.uid, adapter)

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
