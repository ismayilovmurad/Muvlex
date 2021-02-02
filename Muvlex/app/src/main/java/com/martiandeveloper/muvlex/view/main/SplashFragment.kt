package com.martiandeveloper.muvlex.view.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentSplashBinding
import com.martiandeveloper.muvlex.utils.navigate
import com.martiandeveloper.muvlex.utils.networkAvailable
import com.martiandeveloper.muvlex.viewmodel.main.SplashViewModel

class SplashFragment : Fragment() {

    private lateinit var splashViewModel: SplashViewModel

    private lateinit var fragmentSplashBinding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        fragmentSplashBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        fragmentSplashBinding.lifecycleOwner = viewLifecycleOwner

        observe()

        animateLogo()

        decideWhereToGo()

        return fragmentSplashBinding.root
    }

    private fun observe() {

        splashViewModel.feedEnable.observe(viewLifecycleOwner, {
            view.navigate(if (it) SplashFragmentDirections.actionSplashFragmentToFeedFragment() else SplashFragmentDirections.actionSplashFragmentToLogInFragment())
        })

    }

    private fun animateLogo() {
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 1000
        fragmentSplashBinding.fragmentSplashLogoIV.startAnimation(alphaAnimation)
    }

    private fun decideWhereToGo() {

        Handler(Looper.getMainLooper()).postDelayed({
            if (Firebase.auth.currentUser != null)
                if (networkAvailable) splashViewModel.isEmailVerified(Firebase.auth.currentUser!!) else view.navigate(
                    SplashFragmentDirections.actionSplashFragmentToLogInFragment()
                )
            else view.navigate(SplashFragmentDirections.actionSplashFragmentToLogInFragment())
        }, 2000)

    }

}
