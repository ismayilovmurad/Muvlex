package com.martiandeveloper.muvlex.view.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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

                activity?.hideKeyboard()

                if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                    if (passwordETText.value != confirmPasswordETText.value) R.string.passwords_dont_match.showToast(
                        requireContext()
                    ) else signUp()
                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                if (it) openProgressDialog() else progressDialog.dismiss()
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                when (it) {
                    INVALID_EMAIL_FORMAT -> R.string.enter_a_valid_email_address
                    INVALID_PASSWORD -> R.string.password_should_be_at_least_6_characters
                    UNVERIFIED_EMAIL -> R.string.verify_your_email_address_to_continue
                    EMAIL_ALREADY_EXISTS -> R.string.email_address_is_already_in_use_by_another_account
                    else -> R.string.something_went_wrong_try_again_later
                }.showToast(requireContext())

            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(
                    getString(

                        when (it) {
                            CREATE -> R.string.creating_user
                            SEND_VERIFICATION -> R.string.sending_verification_code
                            else -> R.string.blank_message
                        }

                    )
                )
            })

            successADOpen.observe(viewLifecycleOwner, {
                if (it) openSuccessDialog() else successDialog.dismiss()
            })

            logInMTVClick.observe(viewLifecycleOwner, EventObserver {
                view.navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
            })

            emailVerificationSuccessful.observe(viewLifecycleOwner, {
                view.navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpUsernameFragment())
            })

            continueMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                    isContinueMBTNEnable(false)
                    isEmailVerified()
                }

            })

        }

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

    private fun openProgressDialog() {

        val binding = DialogProgressSignUpBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

    private fun openSuccessDialog() {

        val binding = DialogSignUpVerificationBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        viewModel.continueMBTNEnable.observe(viewLifecycleOwner, {

            with(binding.dialogSignUpVerificationContinueMBTN) {
                if (it) enable(requireContext()) else disable(requireContext())
            }

        })

        successDialog.show(binding.root)

    }

}
