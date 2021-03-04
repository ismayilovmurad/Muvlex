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
import com.martiandeveloper.muvlex.databinding.FragmentRateMovieBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.RateMovieViewModel

class RateMovieFragment : Fragment(), RatingBar.OnRatingBarChangeListener {

    private lateinit var viewModel: RateMovieViewModel

    private lateinit var binding: FragmentRateMovieBinding

    private val args: RateMovieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(RateMovieViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rate_movie, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.fragmentRateMovieMainRB.onRatingBarChangeListener = this
        }

        setViewData()

        return binding.root

    }

    private fun setViewData() {

        var genre = ""

        with(viewModel) {

            if (args.movie.id != 0) {

                binding.fragmentRateMoviePosterIV.load(
                    requireContext(),
                    if (args.movie.posterPath.check()) BASE_URL_POSTER + args.movie.posterPath else null
                )

                setReleaseDate(
                    if (args.movie.releaseDate.check()) args.movie.releaseDate!! else getString(
                        R.string.unknown
                    )
                )

                setVoteAverage(
                    if (args.movie.voteAverage != null) args.movie.voteAverage.toString() else getString(
                        R.string.unknown
                    )
                )

                if (!args.movie.genreIds.isNullOrEmpty())

                    for (i in args.movie.genreIds!!.indices) {

                        with(args.movie.genreIds!![i]) {

                            when {
                                this == 28 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Action" else "Action, "
                                this == 12 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Adventure" else "Adventure, "
                                this == 16 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Animation" else "Animation, "
                                this == 35 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Comedy" else "Comedy, "
                                this == 80 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Crime" else "Crime, "
                                this == 99 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Documentary" else "Documentary, "
                                this == 18 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Drama" else "Drama, "
                                this == 10751 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Family" else "Family, "
                                this == 14 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Fantasy" else "Fantasy, "
                                this == 36 -> genre += if (args.movie.genreIds!!.lastIndex == i) "History" else "History, "
                                this == 27 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Horror" else "Horror, "
                                this == 10402 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Music" else "Music, "
                                this == 9648 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Mystery" else "Mystery, "
                                this == 10749 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Romance" else "Romance, "
                                this == 878 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Science Fiction" else "Science Fiction, "
                                this == 10770 -> genre += if (args.movie.genreIds!!.lastIndex == i) "TV Movie" else "TV Movie, "
                                this == 53 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Thriller" else "Thriller, "
                                this == 10752 -> genre += if (args.movie.genreIds!!.lastIndex == i) "War" else "War, "
                                this == 37 -> genre += if (args.movie.genreIds!!.lastIndex == i) "Western" else "Western, "
                            }

                        }

                    }

                setGenre(if (genre != "") genre else getString(R.string.unknown))

                setLanguage(
                    if (args.movie.originalLanguage.check()) args.movie.originalLanguage!! else getString(
                        R.string.unknown
                    )
                )

                setOverview(
                    if (args.movie.overview.check()) args.movie.overview!! else getString(
                        R.string.unknown
                    )
                )

            } else R.string.something_went_wrong_try_again_later.showToast(requireContext())

        }

    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (rating > 0.0) view.navigate(
            RateMovieFragmentDirections.actionRateMovieFragmentToWriteMovieReviewFragment(
                args.movie,
                rating
            )
        )
    }

}
