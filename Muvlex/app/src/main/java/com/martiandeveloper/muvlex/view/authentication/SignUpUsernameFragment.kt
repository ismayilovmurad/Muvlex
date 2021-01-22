package com.martiandeveloper.muvlex.view.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.martiandeveloper.muvlex.databinding.DialogProgressSignUpUsernameBinding
import com.martiandeveloper.muvlex.databinding.FragmentSignUpUsernameBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.utils.PRIVACY_POLICY_URL
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.viewmodel.authentication.SignUpUsernameViewModel
import java.util.regex.Pattern

class SignUpUsernameFragment : Fragment() {

    private lateinit var fragmentSignUpUsernameBinding: FragmentSignUpUsernameBinding

    private lateinit var signUpUsernameViewModel: SignUpUsernameViewModel

    private val pattern = Pattern.compile("""^[_.A-Za-z0-9]*((\s)*[_.A-Za-z0-9])*${'$'}""")

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
            setUsernameErrorMTVText("")
        }

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.StyleDialog).create()

        return fragmentSignUpUsernameBinding.root

    }

    private fun observe() {

        val fragmentSignUpUsernameNextMBTN =
            fragmentSignUpUsernameBinding.fragmentSignUpUsernameNextMBTN

        with(signUpUsernameViewModel) {

            nextMBTNEnable.observe(viewLifecycleOwner, {

                if (it) {

                    with(fragmentSignUpUsernameNextMBTN) {
                        isEnabled = true
                        alpha = 1F
                        setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
                    }

                } else {

                    with(fragmentSignUpUsernameNextMBTN) {
                        isEnabled = false
                        alpha = .5F
                        setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
                    }

                }

            })

            usernameETText.observe(viewLifecycleOwner, {

                if (it.isNullOrEmpty()) {
                    setUsernameErrorMTVText(getString(R.string.username_cannot_be_empty))
                    isUsernameErrorMTVGone(false)
                } else {

                    if (usernameETText.value!!.length < 15) {

                        if (pattern.matcher(it).matches()) {

                            if (isNetworkAvailable) {
                                isUsernameAvailable()
                            } else {
                                setUsernameErrorMTVText(getString(R.string.no_internet_connection))
                                isUsernameErrorMTVGone(false)
                            }

                        } else {
                            setUsernameErrorMTVText(getString(R.string.username_can_only_use_letters_numbers_underscores_and_periods))
                            isUsernameErrorMTVGone(false)
                        }

                    } else {
                        setUsernameErrorMTVText(getString(R.string.username_not_available))
                        isUsernameErrorMTVGone(false)
                    }

                }

            })

            nextMBTNClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {

                    if (isNetworkAvailable) {

                        if (usernameErrorMTVGone.value == true) {

                            if (usernameAvailable.value == true) {

                                if (usernameETText.value.isNullOrEmpty()) {
                                    setUsernameErrorMTVText(getString(R.string.username_cannot_be_empty))
                                    isUsernameErrorMTVGone(false)
                                } else {
                                    saveUsernameAndEmail()
                                }

                            }

                        }

                    } else {
                        showToast(R.string.no_internet_connection)
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

            privacyPolicyMTVClick.observe(viewLifecycleOwner, EventObserver {

                if (it) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)))
                }

            })

            usernameErrorMTVGone.observe(viewLifecycleOwner, {

                if (it) {
                    isNextMBTNEnable(true)
                } else {
                    isNextMBTNEnable(false)
                }

            })

            saveSuccessful.observe(viewLifecycleOwner, {

                if (it) {
                    navigate(SignUpUsernameFragmentDirections.actionSignUpUsernameFragmentToFeedFragment())
                }

            })

            errorMessage.observe(viewLifecycleOwner, EventObserver {
                showToast(R.string.something_went_wrong_try_again_later)
            })

            usernameAvailable.observe(viewLifecycleOwner, {

                if (it) {
                    isUsernameErrorMTVGone(true)
                } else {
                    setUsernameErrorMTVText(getString(R.string.username_not_available))
                    isUsernameErrorMTVGone(false)
                }

            })

            progressMTVTextDecider.observe(viewLifecycleOwner, {

                when (it) {
                    "check" -> setProgressMTVText(getString(R.string.checking_username))
                    "save" -> setProgressMTVText(getString(R.string.saving))
                    else -> setProgressMTVText("")
                }

            })

        }

    }

    private fun setProgress(progress: Boolean) {

        if (progress) {
            signUpUsernameViewModel.isNextMBTNEnable(false)
            openProgressDialog()
        } else {
            progressDialog.dismiss()
        }

    }

    private fun showToast(message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun openProgressDialog() {

        val binding = DialogProgressSignUpUsernameBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.signUpUsernameViewModel = signUpUsernameViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        with(progressDialog) {
            setView(binding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

}
