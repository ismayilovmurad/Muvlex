package com.martiandeveloper.muvlex.view.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogEmailNotVerifiedBinding
import com.martiandeveloper.muvlex.databinding.DialogProgressLogInBinding
import com.martiandeveloper.muvlex.databinding.FragmentLogInBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.authentication.LogInViewModel

class LogInFragment : Fragment() {

    private lateinit var viewModel: LogInViewModel

    private lateinit var binding: FragmentLogInBinding

    private var passwordVisible = false

    private lateinit var progressDialog: AlertDialog
    private lateinit var errorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(LogInViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        setPasswordToggle()

        with(requireContext()) {
            progressDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
            errorDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
        }

        return binding.root

    }

    private fun observe() {

        with(viewModel) {

            logInMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.fragmentLogInLogInMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            emailOrUsernameACTText.observe(viewLifecycleOwner, {
                isLogInMBTNEnable(!it.isNullOrEmpty() && !passwordETText.value.isNullOrEmpty())
            })

            passwordETText.observe(viewLifecycleOwner, {
                isLogInMBTNEnable(!it.isNullOrEmpty() && !emailOrUsernameACTText.value.isNullOrEmpty())
            })

            logInMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                        if (!Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameACTText.value!!)
                                .matches()
                        ) isUsernameExists() else logIn()
                    }

                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                if (it) openProgressDialog() else progressDialog.dismiss()
            })

            signUpMTVClick.observe(viewLifecycleOwner, EventObserver {
                if (it) view.navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
            })

            getHelpMTVClick.observe(viewLifecycleOwner, EventObserver {
                if (it) view.navigate(LogInFragmentDirections.actionLogInFragmentToGetHelpLoggingInFragment())
            })

            logInSuccessful.observe(viewLifecycleOwner, {
                if (it) view.navigate(LogInFragmentDirections.actionLogInFragmentToFeedFragment())
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                with(requireContext()) {

                    when (it) {
                        "The email address is badly formatted." -> R.string.enter_a_valid_email_address.showToast(
                            this
                        )
                        "The given password is invalid. [ Password should be at least 6 characters ]" -> R.string.password_should_be_at_least_6_characters.showToast(
                            this
                        )
                        "The email address is already in use by another account." -> R.string.email_address_is_already_in_use_by_another_account.showToast(
                            this
                        )
                        "Email is not verified" -> isErrorADOpen(true)

                        "There is no user record corresponding to this identifier. The user may have been deleted." -> R.string.we_couldnt_find_info_for_this_account.showToast(
                            this
                        )
                        "The password is invalid or the user does not have a password." -> R.string.invalid_password.showToast(
                            this
                        )
                        "Username not found" -> view.navigate(LogInFragmentDirections.actionLogInFragmentToSignUpUsernameFragment())
                        "no_account" -> R.string.we_couldnt_find_info_for_this_account.showToast(
                            this
                        )
                        "already_verified" -> R.string.email_address_is_already_verified.showToast(
                            this
                        )
                        else -> R.string.something_went_wrong_try_again_later.showToast(
                            this
                        )
                    }

                }

            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(

                    when (it) {
                        "login" -> getString(R.string.logging_in)
                        "load" -> getString(R.string.loading_user_data)
                        "check_username" -> getString(R.string.checking_username)
                        else -> ""
                    }

                )
            })

            errorADOpen.observe(viewLifecycleOwner, {
                if (it) openErrorDialog() else errorDialog.dismiss()
            })

            okayMBTNClick.observe(viewLifecycleOwner, EventObserver {
                if (it) isErrorADOpen(false)
            })

            resendMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                        isResendAndOkayMBTNSEnable(false)
                        resendEmailVerification()
                    }

                }

            })

            resendSuccessful.observe(viewLifecycleOwner, EventObserver {

                isResendAndOkayMBTNSEnable(true)

                if (it) {
                    R.string.check_your_email_to_verify_your_muvlex_account.showToast(requireContext())
                    isErrorADOpen(false)
                }

            })

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPasswordToggle() {

        with(binding.fragmentLogInPasswordET) {

            setOnTouchListener(View.OnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP)

                    if (event.rawX >= right - compoundDrawables[2].bounds.width()
                    ) {

                        passwordVisible = if (passwordVisible) {
                            setCompoundDrawables(R.drawable.ic_visibility_off)
                            false
                        } else {
                            setCompoundDrawables(R.drawable.ic_visibility)
                            true
                        }

                        setSelection(selectionEnd)

                        return@OnTouchListener true

                    }

                false
            })

        }

    }

    private fun openProgressDialog() {

        val binding = DialogProgressLogInBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

    private fun openErrorDialog() {

        val binding = DialogEmailNotVerifiedBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(viewModel) {

            resendAndOkayMBTNSEnable.observe(viewLifecycleOwner, {

                with(binding.dialogEmailNotVerifiedResendMBTN) {
                    if (it) enable(requireContext()) else disable(requireContext())
                }

                with(binding.dialogEmailNotVerifiedOkayMBTN) {
                    if (it) enable(requireContext()) else disable(requireContext())
                }

            })

        }

        errorDialog.show(binding.root)

    }

}
