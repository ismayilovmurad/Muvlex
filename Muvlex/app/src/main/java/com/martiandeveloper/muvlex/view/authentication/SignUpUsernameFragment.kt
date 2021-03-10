package com.martiandeveloper.muvlex.view.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogProgressSignUpUsernameBinding
import com.martiandeveloper.muvlex.databinding.FragmentSignUpUsernameBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.view.main.PrivacyPolicyActivity
import com.martiandeveloper.muvlex.viewmodel.authentication.SignUpUsernameViewModel
import java.util.regex.Pattern

class SignUpUsernameFragment : Fragment() {

    private lateinit var viewModel: SignUpUsernameViewModel

    private lateinit var binding: FragmentSignUpUsernameBinding

    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProviders.of(this).get(SignUpUsernameViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_username, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return binding.root

    }

    private fun observe() {

        with(viewModel) {

            nextMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.fragmentSignUpUsernameNextMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            usernameETText.observe(viewLifecycleOwner, {
                setUsernameErrorMTVText(
                    if (it.isNullOrEmpty()) getString(R.string.username_cannot_be_empty)
                    else if (it.length > 15) getString(R.string.username_not_available)
                    else if (!Pattern.compile("""^[_.A-Za-z0-9]*((\s)*[_.A-Za-z0-9])*${'$'}""")
                            .matcher(it).matches()
                    ) getString(R.string.username_can_only_use_letters_numbers_underscores_and_periods)
                    else if (!networkAvailable) getString(
                        R.string.no_internet_connection
                    )
                    else ""
                )
            })

            usernameErrorMTVText.observe(viewLifecycleOwner, {
                if (usernameErrorMTVText.value == "") isUsernameAvailable() else isNextMBTNEnable(
                    false
                )
            })

            usernameAvailable.observe(viewLifecycleOwner, {
                if (!it) setUsernameErrorMTVText(getString(R.string.username_not_available))
                isNextMBTNEnable(it && !usernameETText.value.isNullOrEmpty())
            })

            nextMBTNClick.observe(viewLifecycleOwner, EventObserver {

                activity?.hideKeyboard()

                if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                    if (usernameETText.value.isNullOrEmpty()) setUsernameErrorMTVText(
                        getString(
                            R.string.username_cannot_be_empty
                        )
                    ) else saveUsernameAndEmail()
                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                if (it) openProgressDialog() else dialog.dismiss()
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {
                if (it == USERNAME_NOT_AVAILABLE) setUsernameErrorMTVText(getString(R.string.username_not_available)) else R.string.something_went_wrong_try_again_later.showToast(
                    requireContext()
                )
            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(
                    getString(

                        when (it) {
                            CHECK_USERNAME -> R.string.checking_username
                            SAVE -> R.string.saving
                            else -> R.string.blank_message
                        }

                    )
                )
            })

            saveSuccessful.observe(viewLifecycleOwner, {
                view.navigate(SignUpUsernameFragmentDirections.actionSignUpUsernameFragmentToFeedFragment())
            })

            privacyPolicyMTVClick.observe(viewLifecycleOwner, EventObserver {
                startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            })

        }

    }

    private fun openProgressDialog() {

        val binding = DialogProgressSignUpUsernameBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        dialog.show(binding.root)

    }

}
