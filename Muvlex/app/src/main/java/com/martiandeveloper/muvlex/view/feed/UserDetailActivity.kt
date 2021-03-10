package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityUserDetailBinding
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.viewmodel.feed.UserDetailViewModel

class UserDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: UserDetailViewModel

    private lateinit var binding: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(UserDetailViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        setToolbar()

        with(intent.getStringExtra("username")) {

            if (this != null)
                viewModel.setTitle(

                    when {
                        this.check() -> this
                        else -> getString(R.string.unknown)
                    }

                )

        }

        with(intent.getStringExtra("uid")) {

            if (this != null)
                (supportFragmentManager.findFragmentById(R.id.activity_user_detail_mainFCV) as NavHostFragment).navController.setGraph(
                    R.navigation.user_detail_navigation,
                    UserFragmentArgs(if (this.check()) this else "").toBundle()
                )

        }

    }

    private fun setToolbar() {

        setSupportActionBar(binding.activityUserDetailMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
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

}
