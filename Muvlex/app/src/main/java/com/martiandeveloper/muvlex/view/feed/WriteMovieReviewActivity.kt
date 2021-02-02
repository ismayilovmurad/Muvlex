package com.martiandeveloper.muvlex.view.feed

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityWriteMovieReviewBinding
import com.martiandeveloper.muvlex.utils.load
import com.martiandeveloper.muvlex.viewmodel.feed.WriteMovieReviewViewModel

class WriteMovieReviewActivity : AppCompatActivity() {

    private lateinit var writeMovieReviewViewModel: WriteMovieReviewViewModel

    private lateinit var activityWriteMovieReviewBinding: ActivityWriteMovieReviewBinding

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

        setToolbar()

        setViewData()

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
            setStar(intent.getFloatExtra("rating", 0F))
        }

    }

}
