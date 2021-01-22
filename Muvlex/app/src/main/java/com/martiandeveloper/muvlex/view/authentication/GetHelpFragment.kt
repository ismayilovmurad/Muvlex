package com.martiandeveloper.muvlex.view.authentication

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
import com.martiandeveloper.muvlex.databinding.FragmentGetHelpBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.viewmodel.authentication.GetHelpViewModel

class GetHelpFragment : Fragment() {

    private lateinit var fragmentGetHelpBinding: FragmentGetHelpBinding

    private lateinit var getHelpViewModel: GetHelpViewModel

    private lateinit var progressDialog: AlertDialog
    private lateinit var successDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        getHelpViewModel =
            ViewModelProviders.of(this).get(GetHelpViewModel::class.java)

        fragmentGetHelpBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_get_help,
                container,
                false
            )

        fragmentGetHelpBinding.let {
            it.getHelpLoggingInViewModel = getHelpViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        getHelpViewModel.isContinueMBTNEnable(false)

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()
        successDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return fragmentGetHelpBinding.root

    }

    private fun observe() {

        val fragmentGetHelpLoggingInContinueMBTN =
            fragmentGetHelpBinding.fragmentGetHelpLoggingInContinueMBTN

        with(getHelpViewModel) {

            continueMBTNEnable.observe(viewLifecycleOwner, {

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

            emailOrUsernameETText.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    isContinueMBTNEnable(false)
                } else {
                    isContinueMBTNEnable(true)
                }

            })

            continueMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    with(this) {

                        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameETText.value!!)
                                .matches()
                        ) {

                            if (isNetworkAvailable) {
                                sendPasswordResetEmailForUsername()
                            } else {
                                showToast(R.string.no_internet_connection)
                            }

                        } else {
                            isUsernameExists()
                        }

                    }

                }

            })

            progressADOpen.observe(viewLifecycleOwner, {

                if (it) {
                    setProgress(true)
                } else {
                    setProgress(false)
                }

            })

            sendPasswordResetEmailSuccessful.observe(viewLifecycleOwner, {

                if (it) {
                    isSuccessADOpen(true)
                }

            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {

                    "The email address is badly formatted." -> showToast(R.string.enter_a_valid_email_address)

                    "no_account" -> showToast(R.string.we_couldnt_find_info_for_this_account)

                    else -> showToast(R.string.something_went_wrong_try_again_later)

                }

            })

            progressTVTextDecider.observe(viewLifecycleOwner, {

                when (it) {
                    "send" -> setProgressMTVText(getString(R.string.sending_password_reset_email))
                    "load" -> setProgressMTVText(getString(R.string.loading_user_data))
                    "check_username" -> setProgressMTVText(getString(R.string.checking_username))
                    else -> setProgressMTVText("")
                }

            })

            okayMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    isSuccessADOpen(false)
                    navigate(GetHelpFragmentDirections.actionGetHelpFragmentToLogInFragment())
                }

            })

            successADOpen.observe(viewLifecycleOwner, {

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
            getHelpViewModel.isContinueMBTNEnable(false)
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
            it.getHelpLoggingInViewModel = getHelpViewModel
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
            it.getHelpLoggingInViewModel = getHelpViewModel
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
