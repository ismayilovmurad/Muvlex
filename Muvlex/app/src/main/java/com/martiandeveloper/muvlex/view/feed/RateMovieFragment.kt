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
import timber.log.Timber

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

        //setViewData()

        setListeners()

        Timber.d(args.movie.title)

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

    /*private fun setViewData() {
        if (args.movie.id != 0)

            with(rateMovieViewModel) {
                setTitle(if (args.movie.originalTitle!! != getString(R.string.unknown)) args.movie.originalTitle!! else args.movie.title!!)

                fragmentRateMovieBinding.fragmentRateMoviePosterIV.load(
                    requireContext(),
                    if (args.movie.posterPath != getString(R.string.unknown)) "$BASE_URL_POSTER${args.movie.posterPath}" else null
                )

                setReleaseDate(args.movie.releaseDate!!)

                setVoteAverage(args.movie.voteAverage.toString())

                setGenre(if (args.movie.genreIds!! != "") args.movie.genreIds else resources.getString(R.string.unknown))

                setLanguage(args.movie.originalLanguage)

                setOverview(args.movie.overview)
            }
        else R.string.something_went_wrong_try_again_later.showToast(requireContext())
    }*/

    private fun setListeners() {
        fragmentRateMovieBinding.fragmentRateMovieMainRB.onRatingBarChangeListener = this
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (rating > 0.0) startActivity(
            Intent(
                context,
                WriteMovieReviewActivity::class.java
            ).putExtra(
                "id",
                if (args.movie.id != 0) args.movie.id.toString() else null
            ).putExtra(
                "poster",
                if (args.movie.posterPath != getString(R.string.unknown)) "$BASE_URL_POSTER${args.movie.posterPath}" else null
            ).putExtra(
                "title",
                if (args.movie.originalTitle != getString(R.string.unknown)) args.movie.originalTitle else args.movie.title
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
