package com.martiandeveloper.muvlex.view.main

import android.content.Intent
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

    private lateinit var viewModel: SplashViewModel

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        observe()

        animateLogo()

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        viewModel.decideWhereToGo(binding.root)
    }

    private fun observe() {

        viewModel.feedEnable.observe(viewLifecycleOwner, {

            if (!it) Firebase.auth.signOut()

            with(SplashFragmentDirections) {
                view.navigate(if (it) actionSplashFragmentToFeedFragment() else actionSplashFragmentToLogInFragment())
            }

        })

    }

    private fun animateLogo() {
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 1000
        binding.fragmentSplashLogoIV.startAnimation(alphaAnimation)
    }

}
