package com.martiandeveloper.muvlex.view.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityPrivacyPolicyBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.main.PrivacyPolicyViewModel

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var viewModel: PrivacyPolicyViewModel

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(PrivacyPolicyViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        setToolbar()

        observe()

    }

    private fun setToolbar() {

        setSupportActionBar(binding.activityPrivacyPolicyMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }

    }

    private fun observe() {

        with(viewModel) {

            googlePlayServicesMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {
                show(GOOGLE_PLAY_SERVICES)
            })

            googleAnalyticsForFirebaseMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {
                show(GOOGLE_ANALYTICS_FOR_FIREBASE)
            })

            firebaseCrashlyticsMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {
                show(FIREBASE_CRASHLYTICS)
            })

            facebookMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {
                show(FACEBOOK)
            })

            muvlexGmailMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(APP_EMAIL))

                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    R.string.there_are_no_email_clients_installed.showToast(this@PrivacyPolicyActivity)
                }

            })

            privacyPolicyTemplateMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {
                show(PRIVACY_POLICY_TEMPLATE_LINK)
            })

            appPrivacyPolicyGeneratorMTVClick.observe(this@PrivacyPolicyActivity, EventObserver {
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
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

}
