package com.martiandeveloper.muvlex.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.utils.isNetworkAvailable
import com.martiandeveloper.muvlex.viewmodel.SplashViewModel


class SplashFragment : Fragment() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        observe()

        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 1000
        view.findViewById<ImageView>(R.id.fragment_splash_logoIV).startAnimation(alphaAnimation)

        Handler(Looper.getMainLooper()).postDelayed({

            if (Firebase.auth.currentUser != null) {

                if (isNetworkAvailable) {
                    splashViewModel.checkEmailVerification(Firebase.auth.currentUser!!)
                } else {
                    navigate(SplashFragmentDirections.actionSplashFragmentToLogInFragment())
                }

            } else {
                navigate(SplashFragmentDirections.actionSplashFragmentToLogInFragment())
            }

        }, 2000)

        return view

    }

    private fun observe() {

        splashViewModel.isFeedEnable.observe(viewLifecycleOwner, {

            if (it) {
                navigate(SplashFragmentDirections.actionSplashFragmentToFeedFragment())
            } else {
                navigate(SplashFragmentDirections.actionSplashFragmentToLogInFragment())
            }

        })

    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

}
