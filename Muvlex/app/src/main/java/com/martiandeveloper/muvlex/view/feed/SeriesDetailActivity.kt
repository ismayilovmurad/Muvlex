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
import com.martiandeveloper.muvlex.databinding.ActivitySeriesDetailBinding
import com.martiandeveloper.muvlex.databinding.DialogDiscardDraftSeriesDetailBinding
import com.martiandeveloper.muvlex.model.Series
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.SeriesDetailViewModel

class SeriesDetailActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var viewModel: SeriesDetailViewModel

    private lateinit var binding: ActivitySeriesDetailBinding

    private lateinit var navController: NavController

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SeriesDetailViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_series_detail)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        setToolbar()

        observe()

        navController =
            (supportFragmentManager.findFragmentById(R.id.activity_series_detail_mainFCV) as NavHostFragment).navController

        with(intent.getParcelableExtra<Series>("series")) {

            if (this != null) {
                viewModel.setName(

                    when {
                        this.originalName.check() -> this.originalName!!
                        this.name.check() -> this.name!!
                        else -> getString(R.string.unknown)
                    }

                )

                navController.setGraph(
                    R.navigation.series_detail_navigation,
                    RateSeriesFragmentArgs(this).toBundle()
                )
            }

        }

        dialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()

    }

    private fun setToolbar() {

        setSupportActionBar(binding.activitySeriesDetailMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
            }

    }

    private fun observe() {

        with(viewModel) {

            keepMTVClick.observe(this@SeriesDetailActivity, EventObserver {
                dialog.dismiss()
            })

            discardMTVClick.observe(this@SeriesDetailActivity, EventObserver {
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
        viewModel.isMainMTBGone(destination.label.toString() != "RateSeriesFragment")
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
        if (navController.currentDestination?.label.toString() != "WriteSeriesReviewFragment") super.onBackPressed() else openDiscardDialog()
    }

    private fun openDiscardDialog() {

        val binding = DialogDiscardDraftSeriesDetailBinding.inflate(LayoutInflater.from(this))

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
