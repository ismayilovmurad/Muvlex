package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentRateSeriesBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.RateSeriesViewModel

class RateSeriesFragment : Fragment(), RatingBar.OnRatingBarChangeListener {

    private lateinit var viewModel: RateSeriesViewModel

    private lateinit var binding: FragmentRateSeriesBinding

    private val args: RateSeriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(RateSeriesViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rate_series, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.fragmentRateSeriesMainRB.onRatingBarChangeListener = this
        }

        setViewData()

        return binding.root

    }

    private fun setViewData() {

        var genre = ""

        with(viewModel) {

            if (args.series.id != 0) {

                binding.fragmentRateSeriesPosterIV.load(
                    requireContext(),
                    if (args.series.posterPath.check()) BASE_URL_POSTER + args.series.posterPath else null
                )

                setFirstAirDate(
                    if (args.series.firstAirDate.check()) args.series.firstAirDate!! else getString(
                        R.string.unknown
                    )
                )

                setVoteAverage(
                    if (args.series.voteAverage != null) args.series.voteAverage.toString() else getString(
                        R.string.unknown
                    )
                )

                if (!args.series.genreIds.isNullOrEmpty())

                    for (i in args.series.genreIds!!.indices) {

                        with(args.series.genreIds!![i]) {

                            when {
                                this == 10759 -> genre += if (args.series.genreIds!!.lastIndex == i) "Action & Adventure" else "Action & Adventure, "
                                this == 16 -> genre += if (args.series.genreIds!!.lastIndex == i) "Animation" else "Animation, "
                                this == 35 -> genre += if (args.series.genreIds!!.lastIndex == i) "Comedy" else "Comedy, "
                                this == 80 -> genre += if (args.series.genreIds!!.lastIndex == i) "Crime" else "Crime, "
                                this == 99 -> genre += if (args.series.genreIds!!.lastIndex == i) "Documentary" else "Documentary, "
                                this == 18 -> genre += if (args.series.genreIds!!.lastIndex == i) "Drama" else "Drama, "
                                this == 10751 -> genre += if (args.series.genreIds!!.lastIndex == i) "Family" else "Family, "
                                this == 10762 -> genre += if (args.series.genreIds!!.lastIndex == i) "Kids" else "Kids, "
                                this == 9648 -> genre += if (args.series.genreIds!!.lastIndex == i) "Mystery" else "Mystery, "
                                this == 10763 -> genre += if (args.series.genreIds!!.lastIndex == i) "News" else "News, "
                                this == 10764 -> genre += if (args.series.genreIds!!.lastIndex == i) "Reality" else "Reality, "
                                this == 10765 -> genre += if (args.series.genreIds!!.lastIndex == i) "Sci-Fi & Fantasy" else "Sci-Fi & Fantasy, "
                                this == 10766 -> genre += if (args.series.genreIds!!.lastIndex == i) "Soap" else "Soap, "
                                this == 10767 -> genre += if (args.series.genreIds!!.lastIndex == i) "Talk" else "Talk, "
                                this == 10768 -> genre += if (args.series.genreIds!!.lastIndex == i) "War & Politics" else "War & Politics, "
                                this == 37 -> genre += if (args.series.genreIds!!.lastIndex == i) "Western" else "Western, "
                            }

                        }

                    }

                setGenre(if (genre != "") genre else getString(R.string.unknown))

                setLanguage(
                    if (args.series.originalLanguage.check()) args.series.originalLanguage!! else getString(
                        R.string.unknown
                    )
                )

                setOverview(
                    if (args.series.overview.check()) args.series.overview!! else getString(
                        R.string.unknown
                    )
                )

            } else R.string.something_went_wrong_try_again_later.showToast(requireContext())

        }

    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (rating > 0.0) view.navigate(
            RateSeriesFragmentDirections.actionRateSeriesFragmentToWriteSeriesReviewFragment(
                args.series,
                rating
            )
        )
    }

}
