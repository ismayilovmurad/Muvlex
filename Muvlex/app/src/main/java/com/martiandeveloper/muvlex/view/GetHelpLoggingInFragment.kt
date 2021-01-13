package com.martiandeveloper.muvlex.view

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogPasswordResetBinding
import com.martiandeveloper.muvlex.databinding.DialogProgressGetHelpLoggingInBinding
import com.martiandeveloper.muvlex.databinding.FragmentGetHelpLoggingInBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.viewmodel.GetHelpLoggingInViewModel

class GetHelpLoggingInFragment : Fragment() {

    private lateinit var fragmentGetHelpLoggingInBinding: FragmentGetHelpLoggingInBinding

    private lateinit var getHelpLoggingInViewModel: GetHelpLoggingInViewModel

    private lateinit var progressDialog: AlertDialog
    private lateinit var successDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        getHelpLoggingInViewModel =
            ViewModelProviders.of(this).get(GetHelpLoggingInViewModel::class.java)

        fragmentGetHelpLoggingInBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_get_help_logging_in,
                container,
                false
            )

        fragmentGetHelpLoggingInBinding.let {
            it.getHelpLoggingInViewModel = getHelpLoggingInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        getHelpLoggingInViewModel.setLogInButtonEnable(false)

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()
        successDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return fragmentGetHelpLoggingInBinding.root

    }

    private fun observe() {

        val fragmentGetHelpLoggingInContinueMBTN =
            fragmentGetHelpLoggingInBinding.fragmentGetHelpLoggingInContinueMBTN

        with(getHelpLoggingInViewModel) {

            isContinueButtonEnable.observe(viewLifecycleOwner, {

                if (it) {

                    with(fragmentGetHelpLoggingInContinueMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                } else {

                    with(fragmentGetHelpLoggingInContinueMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                }

            })

            emailOrUsernameETContent.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    setLogInButtonEnable(false)
                } else {
                    setLogInButtonEnable(true)
                }

            })

            onContinueButtonClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    with(this) {

                        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameETContent.value!!)
                                .matches()
                        ) {

                            if (isNetworkAvailable) {
                                sendResetPassword()
                            } else {
                                showToast(R.string.no_internet_connection)
                            }

                        } else {
                            checkIfUsernameExists()
                        }

                    }

                }

            })

            isProgressDialogOpen.observe(viewLifecycleOwner, {

                if (it) {
                    setProgress(true)
                } else {
                    setProgress(false)
                }

            })

            isSendingSuccessful.observe(viewLifecycleOwner, {

                if (it) {
                    setSuccessDialogOpen(true)
                }

            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {

                    "The email address is badly formatted." -> showToast(R.string.enter_a_valid_email_address)

                    "no_account" -> showToast(R.string.we_couldnt_find_info_for_this_account)

                    else -> showToast(R.string.something_went_wrong_try_again_later)

                }

            })

            progressTextDecider.observe(viewLifecycleOwner, {

                when (it) {
                    "send" -> setProgressText(getString(R.string.sending_password_reset_email))
                    "load" -> setProgressText(getString(R.string.loading_user_data))
                    "check_username" -> setProgressText(getString(R.string.checking_username))
                    else -> setProgressText("")
                }

            })

            onOkayButtonClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    setSuccessDialogOpen(false)
                    navigate(GetHelpLoggingInFragmentDirections.actionGetHelpLoggingInFragmentToLogInFragment())
                }

            })

            isSuccessDialogOpen.observe(viewLifecycleOwner, {

                if (it) {
                    openSuccessDialog()
                } else {
                    successDialog.dismiss()
                }

            })

        }

    }

    private fun setProgress(progress: Boolean) {

        if (progress) {
            getHelpLoggingInViewModel.setLogInButtonEnable(false)
            openProgressDialog()
        } else {
            progressDialog.dismiss()
        }

    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    private fun showToast(message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun openProgressDialog() {

        val binding = DialogProgressGetHelpLoggingInBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.getHelpLoggingInViewModel = getHelpLoggingInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(progressDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

    }

    private fun openSuccessDialog() {

        val binding = DialogPasswordResetBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.getHelpLoggingInViewModel = getHelpLoggingInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(successDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

    }

}
