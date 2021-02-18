package com.martiandeveloper.muvlex.view.main

import android.os.Bundle
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

        return fragmentSplashBinding.root

    }

    override fun onResume() {
        super.onResume()
        splashViewModel.decideWhereToGo(fragmentSplashBinding.root)
    }

    private fun observe() {

        splashViewModel.feedEnable.observe(viewLifecycleOwner, {

            if (!it) Firebase.auth.signOut()

            with(SplashFragmentDirections) {
                view.navigate(if (it) actionSplashFragmentToFeedFragment() else actionSplashFragmentToLogInFragment())
            }

        })

    }

    private fun animateLogo() {
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 1000
        fragmentSplashBinding.fragmentSplashLogoIV.startAnimation(alphaAnimation)
    }

}
