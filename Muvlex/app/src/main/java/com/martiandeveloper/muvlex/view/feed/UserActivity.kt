package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.ProfilePostAdapter
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.databinding.ActivityUserBinding
import com.martiandeveloper.muvlex.model.User
import com.martiandeveloper.muvlex.model.UserPost
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.utils.load
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.utils.showToast
import com.martiandeveloper.muvlex.viewmodel.feed.UserViewModel
import timber.log.Timber

class UserActivity : AppCompatActivity(), UserPostAdapter.ItemClickListener {

    private lateinit var viewModel: UserViewModel

    private lateinit var binding: ActivityUserBinding

    private lateinit var userPostAdapter: UserPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        setToolbar()

        setViewData()

        observe()

        viewModel.setFollowMBTNText(getString(R.string.follow))

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {

        userPostAdapter = UserPostAdapter(this)

        binding.activityUserMainRB.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = userPostAdapter
        }

        if(intent.getParcelableExtra<User>("user") != null){
            if(intent.getParcelableExtra<User>("user")!!.uid != null){
                Timber.d(intent.getParcelableExtra<User>("user")!!.uid!!.toString())
                viewModel.getData(userPostAdapter,intent.getParcelableExtra<User>("user")!!.uid!!.toString())
            }

        }


    }

    private fun setToolbar() {

        setSupportActionBar(binding.activityUserMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
            }

    }

    private fun setViewData() {

        with(intent.getParcelableExtra<User>("user")) {

            if (this != null) {

                viewModel.setUsername(if (username.check()) username!! else getString(R.string.unknown))

                binding.recyclerviewMovieItemPosterIV.load(
                    this@UserActivity,
                    if (picture.check()) picture else null
                )

                viewModel.setFollowers(followers?.size?.toString() ?: getString(R.string.unknown))

                viewModel.setFollowing(following?.size?.toString() ?: getString(R.string.unknown))

            }

        }

    }

    private fun observe() {

        with(viewModel) {

            viewModel.followMBTNClick.observe(this@UserActivity, {

                with(intent.getParcelableExtra<User>("user")) {

                    if (networkAvailable) {
                        if (this != null && this.uid.check()) follow(this.uid!!)
                    } else R.string.no_internet_connection.showToast(this@UserActivity)

                }

            })

            errorMessage.observe(this@UserActivity, {
                R.string.something_went_wrong_try_again_later.showToast(this@UserActivity)
            })

            followSuccessful.observe(this@UserActivity, {
                setFollowMBTNText(getString(R.string.unfollow))
            })

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

    override fun onItemClick(UserPost: UserPost) {
        TODO("Not yet implemented")
    }

}
