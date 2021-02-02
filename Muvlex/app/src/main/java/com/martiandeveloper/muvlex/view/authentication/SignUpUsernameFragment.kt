package com.martiandeveloper.muvlex.view.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.DialogProgressSignUpUsernameBinding
import com.martiandeveloper.muvlex.databinding.FragmentSignUpUsernameBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.authentication.SignUpUsernameViewModel
import java.util.regex.Pattern

class SignUpUsernameFragment : Fragment() {

    private lateinit var signUpUsernameViewModel: SignUpUsernameViewModel

    private lateinit var fragmentSignUpUsernameBinding: FragmentSignUpUsernameBinding

    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        signUpUsernameViewModel =
            ViewModelProviders.of(this).get(SignUpUsernameViewModel::class.java)

        fragmentSignUpUsernameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_username, container, false)

        fragmentSignUpUsernameBinding.let {
            it.signUpUsernameViewModel = signUpUsernameViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        observe()

        with(signUpUsernameViewModel) {
            isUsernameErrorMTVGone(true)
            isUsernamePBGone(true)
            setUsernameErrorMTVText("no_error")
        }

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return fragmentSignUpUsernameBinding.root

    }

    private fun observe() {

        with(signUpUsernameViewModel) {

            nextMBTNEnable.observe(viewLifecycleOwner, {

                with(fragmentSignUpUsernameBinding.fragmentSignUpUsernameNextMBTN) {
                    if (it) enable(context) else disable(context)
                }

            })

            usernameETText.observe(viewLifecycleOwner, {

                setUsernameErrorMTVText(
                    if (it.isNullOrEmpty()) getString(R.string.username_cannot_be_empty) else if (usernameETText.value!!.length > 15) getString(
                        R.string.username_not_available
                    ) else if (!Pattern.compile("""^[_.A-Za-z0-9]*((\s)*[_.A-Za-z0-9])*${'$'}""")
                            .matcher(it).matches()
                    ) getString(R.string.username_can_only_use_letters_numbers_underscores_and_periods) else if (!networkAvailable) getString(
                        R.string.no_internet_connection
                    ) else "no_error"
                )

                isUsernameErrorMTVGone(usernameErrorMTVText.value == "no_error")

                if (usernameErrorMTVText.value == "no_error") isUsernameAvailable() else isNextMBTNEnable(
                    false
                )

            })

            nextMBTNClick.observe(viewLifecycleOwner, EventObserver {
                if (it)

                    if (networkAvailable) {
                        if (usernameErrorMTVGone.value == true)
                            if (usernameAvailable.value == true)

                                if (usernameETText.value.isNullOrEmpty()) {
                                    setUsernameErrorMTVText(getString(R.string.username_cannot_be_empty))
                                    isUsernameErrorMTVGone(false)
                                } else saveUsernameAndEmail()

                    } else R.string.no_internet_connection.showToast(requireContext())

            })

            progressADOpen.observe(viewLifecycleOwner, {
                setProgress(it)
            })

            privacyPolicyMTVClick.observe(viewLifecycleOwner, EventObserver {
                if (it) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)))
            })

            usernameErrorMTVGone.observe(viewLifecycleOwner, {
                isNextMBTNEnable(it)
            })

            saveSuccessful.observe(viewLifecycleOwner, {
                if (it) view.navigate(SignUpUsernameFragmentDirections.actionSignUpUsernameFragmentToFeedFragment())
            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {
                R.string.something_went_wrong_try_again_later.showToast(requireContext())
            })

            usernameAvailable.observe(viewLifecycleOwner, {

                if (it) isUsernameErrorMTVGone(true) else {
                    setUsernameErrorMTVText(getString(R.string.username_not_available))
                    isUsernameErrorMTVGone(false)
                }

            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {
                setProgressMTVText(

                    when (it) {
                        "check" -> getString(R.string.checking_username)
                        "save" -> getString(R.string.saving)
                        else -> ""
                    }

                )
            })

        }

    }

    private fun setProgress(progress: Boolean) {

        if (progress) {
            signUpUsernameViewModel.isNextMBTNEnable(false)
            openProgressDialog()
        } else progressDialog.dismiss()

    }

    private fun openProgressDialog() {

        val binding = DialogProgressSignUpUsernameBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.signUpUsernameViewModel = signUpUsernameViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        progressDialog.show(binding.root)

    }

}
