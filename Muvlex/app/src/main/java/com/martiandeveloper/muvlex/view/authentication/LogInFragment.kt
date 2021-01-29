package com.martiandeveloper.muvlex.view.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogEmailNotVerifiedBinding
import com.martiandeveloper.muvlex.databinding.DialogProgressLogInBinding
import com.martiandeveloper.muvlex.databinding.FragmentLogInBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.authentication.LogInViewModel

class LogInFragment : Fragment() {

    private lateinit var logInViewModel: LogInViewModel

    private lateinit var fragmentLogInBinding: FragmentLogInBinding

    private var isPasswordVisible = false

    private lateinit var progressDialog: AlertDialog
    private lateinit var errorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        logInViewModel = ViewModelProviders.of(this).get(LogInViewModel::class.java)

        fragmentLogInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)

        fragmentLogInBinding.let {
            it.logInViewModel = logInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        with(logInViewModel) {
            isLogInMBTNEnable(false)
            isErrorADIVGone(false)
            isErrorADPBGone(true)
        }

        setPasswordToggle()

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()
        errorDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return fragmentLogInBinding.root

    }

    private fun observe() {

        with(logInViewModel) {

            logInMBTNEnable.observe(viewLifecycleOwner, {

                with(fragmentLogInBinding.fragmentLogInLogInMBTN) {
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
                if (it)
                    if (networkAvailable)
                        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameACTText.value!!)
                                .matches()
                        )
                            logIn()
                        else
                            isUsernameExists()
                    else R.string.no_internet_connection.showToast(requireContext())
            })

            progressADOpen.observe(viewLifecycleOwner, {
                setProgress(it)
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

                if (it) {
                    isErrorADOpen(false)
                    Firebase.auth.signOut()
                }

            })

            resendMBTNClick.observe(viewLifecycleOwner, EventObserver {
                if (it)
                    if (networkAvailable) {
                        isResendAndOkayMBTNSEnable(false)
                        resendEmailVerification()
                    } else R.string.no_internet_connection.showToast(requireContext())
            })

            resendSuccessful.observe(viewLifecycleOwner, EventObserver {

                isResendAndOkayMBTNSEnable(true)

                if (it) {
                    R.string.check_your_email_to_verify_your_muvlex_account.showToast(requireContext())
                    isErrorADOpen(false)
                    Firebase.auth.signOut()
                }

            })

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPasswordToggle() {

        val fragmentLogInPasswordET = fragmentLogInBinding.fragmentLogInPasswordET

        fragmentLogInPasswordET.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP)

                if (event.rawX >= fragmentLogInPasswordET.right - fragmentLogInPasswordET.compoundDrawables[2].bounds.width()
                ) {

                    isPasswordVisible = if (isPasswordVisible) {

                        with(fragmentLogInPasswordET) {
                            setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_visibility_off,
                                0
                            )
                            transformationMethod =
                                PasswordTransformationMethod.getInstance()
                        }

                        false

                    } else {

                        with(fragmentLogInPasswordET) {
                            setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_visibility,
                                0
                            )
                            transformationMethod =
                                HideReturnsTransformationMethod.getInstance()
                        }

                        true

                    }

                    fragmentLogInPasswordET.setSelection(fragmentLogInPasswordET.selectionEnd)

                    return@OnTouchListener true

                }

            false
        })

    }

    private fun setProgress(progress: Boolean) {

        if (progress) {
            logInViewModel.isLogInMBTNEnable(false)
            openProgressDialog()
        } else progressDialog.dismiss()

    }

    private fun openProgressDialog() {

        val binding = DialogProgressLogInBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.logInViewModel = logInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

    private fun openErrorDialog() {

        val binding = DialogEmailNotVerifiedBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.logInViewModel = logInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        val dialogEmailNotVerifiedResendMBTN = binding.dialogEmailNotVerifiedResendMBTN
        val dialogEmailNotVerifiedOkayMBTN = binding.dialogEmailNotVerifiedOkayMBTN

        with(logInViewModel) {

            resendAndOkayMBTNSEnable.observe(viewLifecycleOwner, {

                if (it) {
                    dialogEmailNotVerifiedResendMBTN.enable(requireContext())
                    dialogEmailNotVerifiedOkayMBTN.enable(requireContext())
                } else {
                    dialogEmailNotVerifiedResendMBTN.disable(requireContext())
                    dialogEmailNotVerifiedOkayMBTN.disable(requireContext())
                }

            })

        }

        errorDialog.show(binding.root)

    }

}
