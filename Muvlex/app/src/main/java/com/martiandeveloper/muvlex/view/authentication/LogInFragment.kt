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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogEmailNotVerifiedBinding
import com.martiandeveloper.muvlex.databinding.DialogProgressLogInBinding
import com.martiandeveloper.muvlex.databinding.FragmentLogInBinding
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.viewmodel.authentication.LogInViewModel

class LogInFragment : Fragment() {

    private lateinit var fragmentLogInBinding: FragmentLogInBinding

    private lateinit var logInViewModel: LogInViewModel

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

        val fragmentLogInLogInMBTN = fragmentLogInBinding.fragmentLogInLogInMBTN

        with(logInViewModel) {

            logInMBTNEnable.observe(viewLifecycleOwner, {

                if (it) {

                    with(fragmentLogInLogInMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                } else {

                    with(fragmentLogInLogInMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                }

            })

            emailOrUsernameACTText.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    isLogInMBTNEnable(false)
                } else {

                    if (passwordETText.value.isNullOrEmpty()) {
                        isLogInMBTNEnable(false)
                    } else {
                        isLogInMBTNEnable(true)
                    }

                }

            })

            passwordETText.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    isLogInMBTNEnable(false)
                } else {

                    if (emailOrUsernameACTText.value.isNullOrEmpty()) {
                        isLogInMBTNEnable(false)
                    } else {
                        isLogInMBTNEnable(true)
                    }

                }

            })

            logInMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    with(this) {

                        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameACTText.value!!)
                                .matches()
                        ) {

                            if (isNetworkAvailable) {
                                logIn()
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

            signUpMTVClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
                }

            })

            getHelpMTVClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    navigate(LogInFragmentDirections.actionLogInFragmentToGetHelpLoggingInFragment())
                }

            })

            logInSuccessful.observe(viewLifecycleOwner, {

                if (it) {
                    navigate(LogInFragmentDirections.actionLogInFragmentToFeedFragment())
                }

            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {

                    "The email address is badly formatted." -> showToast(R.string.enter_a_valid_email_address)

                    "The given password is invalid. [ Password should be at least 6 characters ]" -> showToast(
                        R.string.password_should_be_at_least_6_characters
                    )

                    "The email address is already in use by another account." -> showToast(R.string.email_address_is_already_in_use_by_another_account)

                    "Email is not verified" -> isErrorADOpen(true)

                    "There is no user record corresponding to this identifier. The user may have been deleted." -> showToast(
                        R.string.we_couldnt_find_info_for_this_account
                    )

                    "The password is invalid or the user does not have a password." -> showToast(R.string.invalid_password)

                    "Username not found" -> navigate(LogInFragmentDirections.actionLogInFragmentToSignUpUsernameFragment())

                    "no_account" -> showToast(R.string.we_couldnt_find_info_for_this_account)

                    "already_verified" -> showToast(R.string.email_address_is_already_verified)

                    else -> showToast(R.string.something_went_wrong_try_again_later)

                }

            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {

                when (it) {
                    "login" -> setProgressMTVText(getString(R.string.logging_in))
                    "load" -> setProgressMTVText(getString(R.string.loading_user_data))
                    "check_username" -> setProgressMTVText(getString(R.string.checking_username))
                    else -> setProgressMTVText("")
                }

            })

            errorADOpen.observe(viewLifecycleOwner, {

                if (it) {
                    openErrorDialog()
                } else {
                    errorDialog.dismiss()
                }

            })

            okayMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    isErrorADOpen(false)
                    Firebase.auth.signOut()
                }

            })

            resendMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (isNetworkAvailable) {
                        isResendAndOkayMBTNSEnable(false)
                        resendEmailVerification()
                    } else {
                        showToast(R.string.no_internet_connection)
                    }

                }

            })

            resendSuccessful.observe(viewLifecycleOwner, EventObserver {

                isResendAndOkayMBTNSEnable(true)

                if (it) {
                    showToast(R.string.check_your_email_to_verify_your_muvlex_account)
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

            if (event.action == MotionEvent.ACTION_UP) {

                if (event.rawX >= fragmentLogInPasswordET.right - fragmentLogInPasswordET.compoundDrawables[2].bounds.width()
                ) {

                    val selection: Int = fragmentLogInPasswordET.selectionEnd
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

                    fragmentLogInPasswordET.setSelection(selection)

                    return@OnTouchListener true

                }

            }

            false

        })

    }

    private fun setProgress(progress: Boolean) {

        if (progress) {
            logInViewModel.isLogInMBTNEnable(false)
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

        val binding = DialogProgressLogInBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.logInViewModel = logInViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(progressDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

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

                    with(dialogEmailNotVerifiedResendMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                    with(dialogEmailNotVerifiedOkayMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                } else {

                    with(dialogEmailNotVerifiedResendMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                    with(dialogEmailNotVerifiedOkayMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                }

            })

        }

        with(errorDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

    }

}
