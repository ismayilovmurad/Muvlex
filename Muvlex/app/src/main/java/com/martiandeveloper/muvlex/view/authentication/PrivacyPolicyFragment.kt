package com.martiandeveloper.muvlex.view.authentication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentPrivacyPolicyBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.authentication.PrivacyPolicyViewModel


class PrivacyPolicyFragment : Fragment() {

    private lateinit var viewModel: PrivacyPolicyViewModel

    private lateinit var binding: FragmentPrivacyPolicyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(PrivacyPolicyViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_policy, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setToolbar()

        setHasOptionsMenu(true)

        observe()

        return binding.root

    }

    private fun setToolbar() {

        with(activity as AppCompatActivity) {

            setSupportActionBar(binding.fragmentPrivacyPolicyMainMTB)

            if (supportActionBar != null) with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }

        }

    }

    private fun observe() {

        with(viewModel) {

            googlePlayServicesMTVClick.observe(viewLifecycleOwner, EventObserver {
                show(GOOGLE_PLAY_SERVICES)
            })

            googleAnalyticsForFirebaseMTVClick.observe(viewLifecycleOwner, EventObserver {
                show(GOOGLE_ANALYTICS_FOR_FIREBASE)
            })

            firebaseCrashlyticsMTVClick.observe(viewLifecycleOwner, EventObserver {
                show(FIREBASE_CRASHLYTICS)
            })

            facebookMTVClick.observe(viewLifecycleOwner, EventObserver {
                show(FACEBOOK)
            })

            muvlexGmailMTVClick.observe(viewLifecycleOwner, EventObserver {

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(APP_EMAIL))

                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    R.string.there_are_no_email_clients_installed.showToast(requireContext())
                }

            })

            privacyPolicyTemplateMTVClick.observe(viewLifecycleOwner, EventObserver {
                show(PRIVACY_POLICY_TEMPLATE_LINK)
            })

            appPrivacyPolicyGeneratorMTVClick.observe(viewLifecycleOwner, EventObserver {
                show(APP_PRIVACY_POLICY_GENERATOR)
            })

        }

    }

    private fun show(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

}
