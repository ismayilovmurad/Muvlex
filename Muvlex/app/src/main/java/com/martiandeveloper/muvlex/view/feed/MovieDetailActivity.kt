package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityMovieDetailBinding
import com.martiandeveloper.muvlex.databinding.DialogDiscardDraftMovieDetailBinding
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.MovieDetailViewModel

class MovieDetailActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var viewModel: MovieDetailViewModel

    private lateinit var binding: ActivityMovieDetailBinding

    private lateinit var navController: NavController

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MovieDetailViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        setToolbar()

        observe()

        navController =
            (supportFragmentManager.findFragmentById(R.id.activity_movie_detail_mainFCV) as NavHostFragment).navController

        with(intent.getParcelableExtra<Movie>("movie")) {

            if (this != null) {
                viewModel.setTitle(

                    when {
                        this.originalTitle.check() -> this.originalTitle!!
                        this.title.check() -> this.title!!
                        else -> getString(R.string.unknown)
                    }

                )

                navController.setGraph(
                    R.navigation.movie_detail_navigation,
                    RateMovieFragmentArgs(this).toBundle()
                )
            }

        }

        dialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()

    }

    private fun setToolbar() {

        setSupportActionBar(binding.activityMovieDetailMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
            }

    }

    private fun observe() {

        with(viewModel) {

            keepMTVClick.observe(this@MovieDetailActivity, EventObserver {
                dialog.dismiss()
            })

            discardMTVClick.observe(this@MovieDetailActivity, EventObserver {
                super.onBackPressed()
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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.isMainMTBGone(destination.label.toString() != "RateMovieFragment")
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.label.toString() != "WriteMovieReviewFragment") super.onBackPressed() else openDiscardDialog()
    }

    private fun openDiscardDialog() {

        val binding = DialogDiscardDraftMovieDetailBinding.inflate(LayoutInflater.from(this))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        with(dialog) {
            setView(binding.root)
            show()
        }

    }

}
