package com.martiandeveloper.muvlex.view.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogDiscardDraftSeriesBinding
import com.martiandeveloper.muvlex.databinding.FragmentWriteSeriesReviewBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.view.main.RatingsAndReviewPolicyActivity
import com.martiandeveloper.muvlex.viewmodel.feed.WriteSeriesReviewViewModel

class WriteSeriesReviewFragment : Fragment(), RatingBar.OnRatingBarChangeListener {

    private lateinit var viewModel: WriteSeriesReviewViewModel

    private lateinit var binding: FragmentWriteSeriesReviewBinding

    private lateinit var dialog: AlertDialog

    private val args: WriteSeriesReviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProviders.of(this).get(WriteSeriesReviewViewModel::class.java)

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_write_series_review,
                container,
                false
            )


        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setToolbar()

        observe()

        setViewData()

        binding.fragmentWriteSeriesReviewMainRB.onRatingBarChangeListener = this

        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return binding.root

    }

    override fun onResume() {

        super.onResume()

        with(binding.fragmentWriteSeriesReviewReviewET) {
            requestFocus()
            requireContext().showKeyboard(this)
        }

    }

    private fun setToolbar() {

        with(activity as AppCompatActivity) {
            setSupportActionBar(binding.fragmentWriteSeriesReviewMainMTB)

            if (supportActionBar != null)

                with(supportActionBar!!) {
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                    setDisplayShowTitleEnabled(false)
                    setHomeAsUpIndicator(R.drawable.ic_close)
                }

        }

    }

    private fun observe() {

        with(viewModel) {

            postMTVClick.observe(viewLifecycleOwner, EventObserver {
                activity?.hideKeyboard()

                with(args.series) {

                    if (networkAvailable) {
                        if (rating.value!! >= .5F) save(this)
                    } else R.string.no_internet_connection.showToast(requireContext())

                }

            })

            learnMoreMTVClick.observe(viewLifecycleOwner, EventObserver {
                startActivity(Intent(context, RatingsAndReviewPolicyActivity::class.java))
            })

            keepMTVClick.observe(viewLifecycleOwner, EventObserver {
                dialog.dismiss()
            })

            discardMTVClick.observe(viewLifecycleOwner, EventObserver {
                activity?.finish()
            })

            saveSuccessful.observe(viewLifecycleOwner, {
                R.string.you_have_just_shared_a_new_review.showToast(requireContext())
                activity?.finish()
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {
                R.string.something_went_wrong_try_again_later.showToast(requireContext())
            })

        }

    }

    private fun setViewData() {

        with(args.series) {

            binding.fragmentWriteSeriesReviewPosterIV.load(
                requireContext(),
                if (this.posterPath.check()) BASE_URL_POSTER + this.posterPath!! else null
            )

            viewModel.setName(if (this.name.check()) this.name!! else getString(R.string.unknown))

        }

        viewModel.setRating(args.rating)

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

        val binding = DialogDiscardDraftSeriesBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        with(dialog) {
            setView(binding.root)
            show()
        }

    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        viewModel.setRating(if (rating < .5F) .5F else rating)
    }

}
