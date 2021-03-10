package com.martiandeveloper.muvlex.view.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityRatingsAndReviewPolicyBinding

class RatingsAndReviewPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingsAndReviewPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ratings_and_review_policy)

        setToolbar()
    }

    private fun setToolbar() {

        setSupportActionBar(binding.activityRatingsAndReviewPolicyMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
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
