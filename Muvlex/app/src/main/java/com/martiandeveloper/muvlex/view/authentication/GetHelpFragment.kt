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

    private lateinit var getHelpViewModel: GetHelpViewModel

    private lateinit var fragmentGetHelpBinding: FragmentGetHelpBinding

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
            it.getHelpViewModel = getHelpViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        getHelpViewModel.isContinueMBTNEnable(false)

        with(requireContext()) {
            progressDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
            successDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
        }

        return fragmentGetHelpBinding.root

    }

    private fun observe() {

        with(getHelpViewModel) {

            continueMBTNEnable.observe(viewLifecycleOwner, {

                with(fragmentGetHelpBinding.fragmentGetHelpLoggingInContinueMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            emailOrUsernameETText.observe(viewLifecycleOwner, {
                isContinueMBTNEnable(!it.isNullOrEmpty())
            })

            continueMBTNClick.observe(viewLifecycleOwner, EventObserver {
                if (it) if (networkAvailable) if (Patterns.EMAIL_ADDRESS.matcher(
                        emailOrUsernameETText.value!!
                    ).matches()
                ) sendPasswordResetEmailForUsername() else isUsernameExists() else R.string.no_internet_connection.showToast(
                    requireContext()
                )
            })

            progressADOpen.observe(viewLifecycleOwner, {
                setProgress(it)
            })

            sendPasswordResetEmailSuccessful.observe(viewLifecycleOwner, {
                isSuccessADOpen(it)
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {
                    "The email address is badly formatted." -> R.string.enter_a_valid_email_address
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

        if (progress) {
            getHelpViewModel.isContinueMBTNEnable(false)
            openProgressDialog()
        } else progressDialog.dismiss()

    }

    private fun openProgressDialog() {

        val binding = DialogProgressGetHelpLoggingInBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.getHelpLoggingInViewModel = getHelpViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

    private fun openSuccessDialog() {

        val binding = DialogPasswordResetBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.getHelpLoggingInViewModel = getHelpViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        successDialog.show(binding.root)

    }

}
