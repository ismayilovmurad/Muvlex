package com.martiandeveloper.muvlex.view.feed

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityWriteSeriesReviewBinding
import com.martiandeveloper.muvlex.databinding.DialogDiscardDraftSeriesBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.WriteSeriesReviewViewModel

class WriteSeriesReviewActivity : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {

    private lateinit var writeSeriesReviewViewModel: WriteSeriesReviewViewModel

    private lateinit var activityWriteSeriesReviewBinding: ActivityWriteSeriesReviewBinding

    private lateinit var discardDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        writeSeriesReviewViewModel =
            ViewModelProviders.of(this).get(WriteSeriesReviewViewModel::class.java)

        activityWriteSeriesReviewBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_write_series_review)

        activityWriteSeriesReviewBinding.let {
            it.writeSeriesReviewViewModel = writeSeriesReviewViewModel
            it.lifecycleOwner = this
        }

        writeSeriesReviewViewModel.isPostPBGone(true)

        observe()

        setToolbar()

        setViewData()

        discardDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()

        setListeners()

    }

    override fun onResume() {

        super.onResume()

        val activityWriteSeriesReviewReviewET =
            activityWriteSeriesReviewBinding.activityWriteSeriesReviewReviewET
        activityWriteSeriesReviewReviewET.requestFocus()

        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            activityWriteSeriesReviewReviewET,
            InputMethodManager.SHOW_IMPLICIT
        )

    }

    private fun observe() {

        with(writeSeriesReviewViewModel) {

            postMTVClick.observe(this@WriteSeriesReviewActivity, EventObserver {
                if (networkAvailable) {
                    val id = intent.getStringExtra("id")
                    if (id != null) if (star.value!! >= .5) save(id)
                } else R.string.no_internet_connection.showToast(
                    applicationContext
                )
            })

            gotItMTVClick.observe(this@WriteSeriesReviewActivity, EventObserver {
                if (it) isNoteCVGone(true)
            })

            learnMoreMTVClick.observe(this@WriteSeriesReviewActivity, EventObserver {
                if (it) startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            RATINGS_AND_REVIEW_POLICY_URL
                        )
                    )
                )
            })

            keepMTVClick.observe(this@WriteSeriesReviewActivity, EventObserver {
                if (it) discardDialog.dismiss()
            })

            discardMTVClick.observe(this@WriteSeriesReviewActivity, EventObserver {
                if (it) onBackPressed()
            })

            saveSuccessful.observe(this@WriteSeriesReviewActivity, {
                if (it) onBackPressed()
            })

            errorMessage.observe(this@WriteSeriesReviewActivity, EventObserver {
                R.string.something_went_wrong_try_again_later.showToast(applicationContext)
            })

        }

    }

    private fun setToolbar() {
        setSupportActionBar(activityWriteSeriesReviewBinding.activityWriteSeriesReviewMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
                setHomeAsUpIndicator(R.drawable.ic_close)
            }

    }

    private fun setViewData() {

        activityWriteSeriesReviewBinding.activityWriteSeriesReviewPosterIV.load(
            this,
            intent.getStringExtra("poster")
        )

        with(writeSeriesReviewViewModel) {
            this.setTitle(intent.getStringExtra("title")!!)
            setStar(intent.getFloatExtra("rating", .5F))
        }

    }

    private fun setListeners() {
        activityWriteSeriesReviewBinding.activityWriteSeriesReviewMainRB.onRatingBarChangeListener =
            this
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        writeSeriesReviewViewModel.setStar(if (rating < .5) .5F else rating)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                openDiscardDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

    private fun openDiscardDialog() {

        val binding =
            DialogDiscardDraftSeriesBinding.inflate(LayoutInflater.from(applicationContext))

        binding.let {
            it.writeSeriesReviewViewModel = writeSeriesReviewViewModel
            it.lifecycleOwner = this
        }

        with(discardDialog) {
            setView(binding.root)
            show()
        }

    }

}
