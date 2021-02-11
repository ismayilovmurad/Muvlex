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
import com.martiandeveloper.muvlex.databinding.ActivityWriteMovieReviewBinding
import com.martiandeveloper.muvlex.databinding.DialogDiscardDraftMovieBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.WriteMovieReviewViewModel

class WriteMovieReviewActivity : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {

    private lateinit var writeMovieReviewViewModel: WriteMovieReviewViewModel

    private lateinit var activityWriteMovieReviewBinding: ActivityWriteMovieReviewBinding

    private lateinit var discardDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        writeMovieReviewViewModel =
            ViewModelProviders.of(this).get(WriteMovieReviewViewModel::class.java)

        activityWriteMovieReviewBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_write_movie_review)

        activityWriteMovieReviewBinding.let {
            it.writeMovieReviewViewModel = writeMovieReviewViewModel
            it.lifecycleOwner = this
        }

        writeMovieReviewViewModel.isPostPBGone(true)

        observe()

        setToolbar()

        setViewData()

        discardDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()

        setListeners()

    }

    override fun onResume() {

        super.onResume()

        val activityWriteMovieReviewReviewET =
            activityWriteMovieReviewBinding.activityWriteMovieReviewReviewET
        activityWriteMovieReviewReviewET.requestFocus()

        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            activityWriteMovieReviewReviewET,
            InputMethodManager.SHOW_IMPLICIT
        )

    }

    private fun observe() {

        with(writeMovieReviewViewModel) {

            postMTVClick.observe(this@WriteMovieReviewActivity, EventObserver {
                if (networkAvailable) {
                    val id = intent.getStringExtra("id")
                    if (id != null) if (star.value!! >= .5) save(
                        id,
                        intent.getStringExtra("poster")!!
                    )
                } else R.string.no_internet_connection.showToast(
                    applicationContext
                )
            })

            gotItMTVClick.observe(this@WriteMovieReviewActivity, EventObserver {
                if (it) isNoteCVGone(true)
            })

            learnMoreMTVClick.observe(this@WriteMovieReviewActivity, EventObserver {
                if (it) startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            RATINGS_AND_REVIEW_POLICY_URL
                        )
                    )
                )
            })

            keepMTVClick.observe(this@WriteMovieReviewActivity, EventObserver {
                if (it) discardDialog.dismiss()
            })

            discardMTVClick.observe(this@WriteMovieReviewActivity, EventObserver {
                if (it) onBackPressed()
            })

            saveSuccessful.observe(this@WriteMovieReviewActivity, {
                if (it) onBackPressed()
            })

            errorMessage.observe(this@WriteMovieReviewActivity, EventObserver {
                R.string.something_went_wrong_try_again_later.showToast(applicationContext)
            })

        }

    }

    private fun setToolbar() {
        setSupportActionBar(activityWriteMovieReviewBinding.activityWriteMovieReviewMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
                setHomeAsUpIndicator(R.drawable.ic_close)
            }

    }

    private fun setViewData() {

        activityWriteMovieReviewBinding.activityWriteMovieReviewPosterIV.load(
            this,
            intent.getStringExtra("poster")
        )

        with(writeMovieReviewViewModel) {
            this.setTitle(intent.getStringExtra("title")!!)
            setStar(intent.getFloatExtra("rating", .5F))
        }

    }

    private fun setListeners() {
        activityWriteMovieReviewBinding.activityWriteMovieReviewMainRB.onRatingBarChangeListener =
            this
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        writeMovieReviewViewModel.setStar(if (rating < .5) .5F else rating)
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
            DialogDiscardDraftMovieBinding.inflate(LayoutInflater.from(applicationContext))

        binding.let {
            it.writeMovieReviewViewModel = writeMovieReviewViewModel
            it.lifecycleOwner = this
        }

        with(discardDialog) {
            setView(binding.root)
            show()
        }

    }

}
