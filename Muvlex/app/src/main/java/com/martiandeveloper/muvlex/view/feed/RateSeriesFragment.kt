package com.martiandeveloper.muvlex.view.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentRateSeriesBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.RateSeriesViewModel

class RateSeriesFragment : Fragment(), RatingBar.OnRatingBarChangeListener {

    private lateinit var rateSeriesViewModel: RateSeriesViewModel

    private lateinit var fragmentRateSeriesBinding: FragmentRateSeriesBinding

    private val args: RateSeriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rateSeriesViewModel = ViewModelProviders.of(this).get(RateSeriesViewModel::class.java)

        fragmentRateSeriesBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rate_series, container, false)

        fragmentRateSeriesBinding.let {
            it.rateSeriesViewModel = rateSeriesViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setToolbar()

        setHasOptionsMenu(true)

        setViewData()

        setListeners()

        return fragmentRateSeriesBinding.root

    }

    private fun setToolbar() {

        with(activity as AppCompatActivity) {
            setSupportActionBar(fragmentRateSeriesBinding.fragmentRateSeriesMainMTB)

            if (supportActionBar != null)

                with(supportActionBar!!) {
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                }
        }

    }

    private fun setViewData() {
        if (args.id != 0)

            with(rateSeriesViewModel) {
                setTitle(if (args.originalName != getString(R.string.unknown)) args.originalName else args.name)

                fragmentRateSeriesBinding.fragmentRateSeriesPosterIV.load(
                    requireContext(),
                    if (args.posterPath != getString(R.string.unknown)) "$BASE_URL_POSTER${args.posterPath}" else null
                )

                setReleaseDate(args.firstAirDate)

                setVoteAverage(args.voteAverage)

                setGenre(if (args.genreIds != "") args.genreIds else resources.getString(R.string.unknown))

                setLanguage(args.originalLanguage)

                setOverview(args.overview)
            }
        else R.string.something_went_wrong_try_again_later.showToast(requireContext())
    }

    private fun setListeners() {
        fragmentRateSeriesBinding.fragmentRateSeriesMainRB.onRatingBarChangeListener = this
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (rating > 0.0) startActivity(
            Intent(
                context,
                WriteSeriesReviewActivity::class.java
            ).putExtra(
                "id",
                if (args.id != 0) args.id.toString() else null
            ).putExtra(
                "poster",
                if (args.posterPath != getString(R.string.unknown)) "$BASE_URL_POSTER${args.posterPath}" else null
            ).putExtra(
                "name",
                if (args.originalName != getString(R.string.unknown)) args.originalName else args.name
            ).putExtra("rating", rating)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                openKeyboardForSearchET = false
                findNavController().navigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

}
