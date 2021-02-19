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

    private lateinit var progressDialog: AlertDialog
    private lateinit var successDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProviders.of(this).get(GetHelpViewModel::class.java)

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_get_help,
                container,
                false
            )

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        with(requireContext()) {
            progressDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
            successDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
        }

        return binding.root

    }

    private fun observe() {

        with(viewModel) {

            continueMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.fragmentGetHelpLoggingInContinueMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            emailOrUsernameETText.observe(viewLifecycleOwner, {
                isContinueMBTNEnable(!it.isNullOrEmpty())
            })

            continueMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {

                        activity?.let { it1 -> hideKeyboard(it1) }

                        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameETText.value!!)
                                .matches()
                        ) sendPasswordResetEmail(null) else isUsernameExists()

                    }

                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                setProgress(it)
            })

            sendPasswordResetEmailSuccessful.observe(viewLifecycleOwner, {
                isSuccessADOpen(true)
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {
                    "The email address is badly formatted." -> R.string.enter_a_valid_email_address
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> R.string.we_couldnt_find_info_for_this_account
                    "no_account" -> R.string.we_couldnt_find_info_for_this_account
                    else -> R.string.something_went_wrong_try_again_later
                }.showToast(requireContext())

            })

            progressTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(

                    when (it) {
                        "send" -> getString(R.string.sending_password_reset_email)
                        "load" -> getString(R.string.loading_user_data)
                        "check_username" -> getString(R.string.checking_username)
                        else -> ""
                    }

                )
            })

            okayMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    isSuccessADOpen(false)
                    view.navigate(GetHelpFragmentDirections.actionGetHelpFragmentToLogInFragment())
                }

            })

            successADOpen.observe(viewLifecycleOwner, {
                if (it) openSuccessDialog() else successDialog.dismiss()
            })

        }

    }

    private fun setProgress(progress: Boolean) {
        if (progress) openProgressDialog() else progressDialog.dismiss()
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
