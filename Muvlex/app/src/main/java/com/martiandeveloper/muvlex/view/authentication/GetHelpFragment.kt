package com.martiandeveloper.muvlex.view.authentication

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogPasswordResetBinding
import com.martiandeveloper.muvlex.databinding.DialogProgressGetHelpLoggingInBinding
import com.martiandeveloper.muvlex.databinding.FragmentGetHelpBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.authentication.GetHelpViewModel

class GetHelpFragment : Fragment() {

    private lateinit var viewModel: GetHelpViewModel

    private lateinit var binding: FragmentGetHelpBinding

    private lateinit var successDialog: AlertDialog
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProviders.of(this).get(GetHelpViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_get_help, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        with(requireContext()) {
            successDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
            progressDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
        }

        return binding.root

    }

    private fun observe() {

        with(viewModel) {

            continueMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.fragmentGetHelpContinueMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            emailOrUsernameETText.observe(viewLifecycleOwner, {
                isContinueMBTNEnable(!it.isNullOrEmpty())
            })

            continueMBTNClick.observe(viewLifecycleOwner, EventObserver {

                activity?.hideKeyboard()

                if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                    if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameETText.value!!)
                            .matches()
                    ) sendPasswordResetEmail(null) else isUsernameExists()
                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                if (it) openProgressDialog() else progressDialog.dismiss()
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {
                    INVALID_EMAIL_FORMAT -> R.string.enter_a_valid_email_address
                    INVALID_USER -> R.string.we_couldnt_find_info_for_this_account
                    else -> R.string.something_went_wrong_try_again_later
                }.showToast(requireContext())

            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(
                    getString(

                        when (it) {
                            CHECK_USERNAME -> R.string.checking_username
                            LOAD -> R.string.loading_user_data
                            SEND -> R.string.sending_password_reset_email
                            else -> R.string.blank_message
                        }

                    )
                )
            })

            successADOpen.observe(viewLifecycleOwner, {
                if (it) openSuccessDialog() else successDialog.dismiss()
            })

            okayMBTNClick.observe(viewLifecycleOwner, EventObserver {
                isSuccessADOpen(false)
                view.navigate(GetHelpFragmentDirections.actionGetHelpFragmentToLogInFragment())
            })

        }

    }

    private fun openProgressDialog() {

        val binding = DialogProgressGetHelpLoggingInBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

    private fun openSuccessDialog() {

        val binding = DialogPasswordResetBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        successDialog.show(binding.root)

    }

}
