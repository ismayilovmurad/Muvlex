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
import com.martiandeveloper.muvlex.databinding.FragmentRateMovieBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.RateMovieViewModel

class RateMovieFragment : Fragment(), RatingBar.OnRatingBarChangeListener {

    private lateinit var rateMovieViewModel: RateMovieViewModel

    private lateinit var fragmentRateMovieBinding: FragmentRateMovieBinding

    private val args: RateMovieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rateMovieViewModel = ViewModelProviders.of(this).get(RateMovieViewModel::class.java)

        fragmentRateMovieBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rate_movie, container, false)

        fragmentRateMovieBinding.let {
            it.rateMovieViewModel = rateMovieViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setToolbar()

        setHasOptionsMenu(true)

        setViewData()

        setListeners()

        return fragmentRateMovieBinding.root

    }

    private fun setToolbar() {

        with(activity as AppCompatActivity) {
            setSupportActionBar(fragmentRateMovieBinding.fragmentRateMovieMainMTB)

            if (supportActionBar != null)

                with(supportActionBar!!) {
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                }
        }

    }

    private fun setViewData() {
        if (args.id != 0)

            with(rateMovieViewModel) {
                setTitle(if (args.originalTitle != getString(R.string.unknown)) args.originalTitle else args.title)

                fragmentRateMovieBinding.fragmentRateMoviePosterIV.load(
                    requireContext(),
                    if (args.posterPath != getString(R.string.unknown)) "$BASE_URL_POSTER${args.posterPath}" else null
                )

                setReleaseDate(args.releaseDate)

                setVoteAverage(args.voteAverage)

                setGenre(if (args.genreIds != "") args.genreIds else resources.getString(R.string.unknown))

                setLanguage(args.originalLanguage)

                setOverview(args.overview)
            }
        else R.string.something_went_wrong_try_again_later.showToast(requireContext())
    }

    private fun setListeners() {
        fragmentRateMovieBinding.fragmentRateMovieMainRB.onRatingBarChangeListener = this
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (rating > 0.0) startActivity(
            Intent(
                context,
                WriteMovieReviewActivity::class.java
            ).putExtra(
                "poster",
                if (args.posterPath != getString(R.string.unknown)) "$BASE_URL_POSTER${args.posterPath}" else null
            ).putExtra(
                "title",
                if (args.originalTitle != getString(R.string.unknown)) args.originalTitle else args.title
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
