package com.martiandeveloper.muvlex.view.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogProgressSignUpBinding
import com.martiandeveloper.muvlex.databinding.DialogSignUpVerificationBinding
import com.martiandeveloper.muvlex.databinding.FragmentSignUpBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.authentication.SignUpViewModel

class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel

    private lateinit var binding: FragmentSignUpBinding

    private var passwordVisible = false

    private lateinit var successDialog: AlertDialog
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
            setPasswordToggle(it.fragmentSignUpPasswordET)
            setPasswordToggle(it.fragmentSignUpConfirmPasswordET)
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

            nextMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.fragmentSignUpNextMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            emailETText.observe(viewLifecycleOwner, {
                isNextMBTNEnable(
                    !it.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailETText.value!!)
                        .matches() && !passwordETText.value.isNullOrEmpty() && !confirmPasswordETText.value.isNullOrEmpty()
                )
            })

            passwordETText.observe(viewLifecycleOwner, {
                isNextMBTNEnable(
                    !it.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailETText.value!!)
                        .matches() && !emailETText.value.isNullOrEmpty() && !confirmPasswordETText.value.isNullOrEmpty()
                )
            })

            confirmPasswordETText.observe(viewLifecycleOwner, {
                isNextMBTNEnable(
                    !it.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailETText.value!!)
                        .matches() && !passwordETText.value.isNullOrEmpty() && !emailETText.value.isNullOrEmpty()
                )
            })

            nextMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                        if (passwordETText.value != confirmPasswordETText.value) R.string.passwords_dont_match.showToast(
                            requireContext()
                        ) else {
                            hideKeyboard()
                            signUp()
                        }
                    }

                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                if (it) openProgressDialog() else progressDialog.dismiss()
            })

            logInMTVClick.observe(viewLifecycleOwner, EventObserver {
                if (it) view.navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
            })

            signUpSuccessful.observe(viewLifecycleOwner, {
                isSuccessADOpen(it)
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {
                    "The email address is badly formatted." -> R.string.enter_a_valid_email_address
                    "The given password is invalid. [ Password should be at least 6 characters ]" -> R.string.password_should_be_at_least_6_characters
                    "The email address is already in use by another account." -> R.string.email_address_is_already_in_use_by_another_account
                    "unverified" -> R.string.verify_your_email_address_to_continue
                    else -> R.string.something_went_wrong_try_again_later
                }.showToast(requireContext())

            })

            continueMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                        isContinueMBTNEnable(false)
                        isEmailVerified()
                    }

                }

            })

            successADOpen.observe(viewLifecycleOwner, {
                if (it) openSuccessDialog() else successDialog.dismiss()
            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(

                    when (it) {
                        "create" -> getString(R.string.creating_user)
                        "verification" -> getString(R.string.sending_verification_code)
                        else -> ""
                    }

                )
            })

            emailVerificationSuccessful.observe(viewLifecycleOwner, {

                isContinueMBTNEnable(true)

                if (it) {
                    isSuccessADOpen(false)
                    view.navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpUsernameFragment())
                }

            })

        }

    }

    private fun hideKeyboard() {
        (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            (activity?.currentFocus ?: View(activity)).windowToken,
            0
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPasswordToggle(editText: EditText) {

        with(editText) {

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

    private fun openSuccessDialog() {

        val binding = DialogSignUpVerificationBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(viewModel) {

            continueMBTNEnable.observe(viewLifecycleOwner, {

                with(binding.dialogSignUpVerificationContinueMBTN) {
                    if (it) enable(requireContext()) else disable(requireContext())
                }

            })

        }

        successDialog.show(binding.root)

    }

    private fun openProgressDialog() {

        val binding = DialogProgressSignUpBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

}
