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

    private lateinit var errorDialog: AlertDialog
    private lateinit var progressDialog: AlertDialog

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
            errorDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
            progressDialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog).create()
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

                activity?.hideKeyboard()

                if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emailOrUsernameACTText.value!!)
                            .matches()
                    ) isUsernameExists() else logInUser()
                }

            })

            progressADOpen.observe(viewLifecycleOwner, {
                if (it) openProgressDialog() else progressDialog.dismiss()
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {

                with(requireContext()) {

                    when (it) {
                        INVALID_EMAIL_FORMAT -> R.string.enter_a_valid_email_address.showToast(
                            this
                        )
                        INVALID_USER -> R.string.we_couldnt_find_info_for_this_account.showToast(
                            this
                        )
                        INVALID_PASSWORD -> R.string.password_should_be_at_least_6_characters.showToast(
                            this
                        )
                        WRONG_PASSWORD -> R.string.invalid_password.showToast(
                            this
                        )
                        USERNAME_NOT_FOUND -> view.navigate(LogInFragmentDirections.actionLogInFragmentToSignUpUsernameFragment())
                        EMAIL_ALREADY_VERIFIED -> R.string.email_address_is_already_verified.showToast(
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
                    getString(

                        when (it) {
                            CHECK_USERNAME -> R.string.checking_username
                            LOAD -> R.string.loading_user_data
                            LOGIN -> R.string.logging_in
                            else -> R.string.blank_message
                        }

                    )
                )
            })

            logInSuccessful.observe(viewLifecycleOwner, {
                view.navigate(LogInFragmentDirections.actionLogInFragmentToFeedFragment())
            })

            errorADOpen.observe(viewLifecycleOwner, {
                if (it) openErrorDialog() else errorDialog.dismiss()
            })

            resendSuccessful.observe(viewLifecycleOwner, EventObserver {
                isResendAndOkayMBTNSEnable(true)

                R.string.check_your_email_to_verify_your_muvlex_account.showToast(requireContext())
            })

            resendMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (!networkAvailable) R.string.no_internet_connection.showToast(requireContext()) else {
                    isResendAndOkayMBTNSEnable(false)
                    resendEmailVerification()
                }

            })

            signUpMTVClick.observe(viewLifecycleOwner, EventObserver {
                view.navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
            })

            getHelpMTVClick.observe(viewLifecycleOwner, EventObserver {
                view.navigate(LogInFragmentDirections.actionLogInFragmentToGetHelpLoggingInFragment())
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

        viewModel.resendAndOkayMBTNSEnable.observe(viewLifecycleOwner, {

            with(binding.dialogEmailNotVerifiedResendMBTN) {
                if (it) enable(requireContext()) else disable(requireContext())
            }

            with(binding.dialogEmailNotVerifiedOkayMBTN) {
                if (it) enable(requireContext()) else disable(requireContext())
            }

        })

        errorDialog.show(binding.root)

    }

}
