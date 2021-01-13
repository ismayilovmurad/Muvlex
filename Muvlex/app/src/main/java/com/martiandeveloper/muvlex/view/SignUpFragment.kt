package com.martiandeveloper.muvlex.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.martiandeveloper.muvlex.databinding.DialogProgressSignUpBinding
import com.martiandeveloper.muvlex.databinding.DialogSignUpVerificationBinding
import com.martiandeveloper.muvlex.databinding.FragmentSignUpBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private lateinit var fragmentSignUpBinding: FragmentSignUpBinding

    private lateinit var signUpViewModel: SignUpViewModel

    private var isPasswordVisible = false

    private lateinit var successDialog: AlertDialog
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)

        fragmentSignUpBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        fragmentSignUpBinding.let {
            it.signUpViewModel = signUpViewModel
            it.lifecycleOwner = viewLifecycleOwner
            setPasswordToggle(it.fragmentSignUpPasswordET)
            setPasswordToggle(it.fragmentSignUpConfirmPasswordET)
        }

        observe()

        with(signUpViewModel) {
            setNextButtonEnable(false)
            setIsSuccessDialogImageGone(false)
            setIsSuccessDialogProgressGone(true)
        }

        successDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()
        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return fragmentSignUpBinding.root

    }

    private fun observe() {

        val fragmentSignUpNextMBTN = fragmentSignUpBinding.fragmentSignUpNextMBTN

        with(signUpViewModel) {

            isNextButtonEnable.observe(viewLifecycleOwner, {

                if (it) {

                    with(fragmentSignUpNextMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                } else {

                    with(fragmentSignUpNextMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                }

            })

            emailETContent.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    setNextButtonEnable(false)
                } else {

                    if (Patterns.EMAIL_ADDRESS.matcher(emailETContent.value!!)
                            .matches()
                    ) {

                        if (passwordETContent.value.isNullOrEmpty()) {

                            setNextButtonEnable(false)

                        } else {

                            if (confirmPasswordETContent.value.isNullOrEmpty()) {
                                setNextButtonEnable(false)
                            } else {
                                setNextButtonEnable(true)
                            }

                        }

                    } else {
                        setNextButtonEnable(false)
                    }

                }

            })

            passwordETContent.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    setNextButtonEnable(false)
                } else {

                    if (emailETContent.value.isNullOrEmpty()) {

                        setNextButtonEnable(false)

                    } else {

                        if (Patterns.EMAIL_ADDRESS.matcher(emailETContent.value!!)
                                .matches()
                        ) {

                            if (confirmPasswordETContent.value.isNullOrEmpty()) {
                                setNextButtonEnable(false)
                            } else {
                                setNextButtonEnable(true)
                            }

                        } else {
                            setNextButtonEnable(false)
                        }

                    }

                }

            })

            confirmPasswordETContent.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    setNextButtonEnable(false)
                } else {

                    if (emailETContent.value.isNullOrEmpty()) {

                        setNextButtonEnable(false)

                    } else {

                        if (Patterns.EMAIL_ADDRESS.matcher(emailETContent.value!!)
                                .matches()
                        ) {

                            if (passwordETContent.value.isNullOrEmpty()) {
                                setNextButtonEnable(false)
                            } else {
                                setNextButtonEnable(true)
                            }

                        } else {
                            setNextButtonEnable(false)
                        }

                    }

                }

            })

            onNextButtonClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (passwordETContent.value != confirmPasswordETContent.value) {
                        Toast.makeText(
                            context,
                            getString(R.string.passwords_dont_match),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        if (isNetworkAvailable) {
                            signUp()
                        } else {
                            showToast(R.string.no_internet_connection)
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

            onLogInTextViewClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
                }

            })

            isSignUpSuccessful.observe(viewLifecycleOwner, {

                if (it) {
                    setSuccessDialogOpen(true)
                }

            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {

                    "The email address is badly formatted." -> showToast(R.string.enter_a_valid_email_address)

                    "The given password is invalid. [ Password should be at least 6 characters ]" -> showToast(
                        R.string.password_should_be_at_least_6_characters
                    )

                    "The email address is already in use by another account." -> showToast(R.string.email_address_is_already_in_use_by_another_account)

                    "unverified" -> showToast(R.string.verify_your_email_address_to_continue)

                    else -> showToast(R.string.something_went_wrong_try_again_later)

                }

            })

            onContinueButtonClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (isNetworkAvailable) {
                        setIsContinueButtonEnable(false)
                        checkEmailVerification()
                    } else {
                        showToast(R.string.no_internet_connection)
                    }

                }

            })

            isSuccessDialogOpen.observe(viewLifecycleOwner, {

                if (it) {
                    openSuccessDialog()
                } else {
                    successDialog.dismiss()
                }

            })

            progressTextDecider.observe(viewLifecycleOwner, {

                when (it) {
                    "create" -> setProgressText(getString(R.string.creating_user))
                    "verification" -> setProgressText(getString(R.string.sending_verification_code))
                    else -> setProgressText("")
                }

            })

            isEmailVerificationSuccessful.observe(viewLifecycleOwner, {

                setIsContinueButtonEnable(true)

                if (it) {
                    setSuccessDialogOpen(false)
                    navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpUsernameFragment())
                }

            })

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPasswordToggle(editText: EditText) {

        editText.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {

                if (event.rawX >= editText.right - editText.compoundDrawables[2].bounds.width()
                ) {

                    val selection: Int = editText.selectionEnd
                    isPasswordVisible = if (isPasswordVisible) {

                        with(editText) {
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

                        with(editText) {
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

                    editText.setSelection(selection)

                    return@OnTouchListener true

                }

            }

            false

        })

    }

    private fun setProgress(progress: Boolean) {

        if (progress) {
            signUpViewModel.setNextButtonEnable(false)
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

    private fun openSuccessDialog() {

        val binding = DialogSignUpVerificationBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.signUpViewModel = signUpViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        val dialogSignUpVerificationContinueMBTN = binding.dialogSignUpVerificationContinueMBTN

        with(signUpViewModel) {

            isContinueButtonEnable.observe(viewLifecycleOwner, {

                if (it) {

                    with(dialogSignUpVerificationContinueMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                } else {

                    with(dialogSignUpVerificationContinueMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                }

            })

        }

        with(successDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

    }

    private fun openProgressDialog() {

        val binding = DialogProgressSignUpBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.signUpViewModel = signUpViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(progressDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

    }

}
