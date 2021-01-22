package com.martiandeveloper.muvlex.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.LanguageAdapter
import com.martiandeveloper.muvlex.databinding.ActivityMainBinding
import com.martiandeveloper.muvlex.databinding.DialogLanguageBinding
import com.martiandeveloper.muvlex.model.Language
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.main.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity(), LanguageAdapter.ItemClickListener,
    NavController.OnDestinationChangedListener {

    private lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private lateinit var adapter: LanguageAdapter

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        checkLanguage()

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.let {
            it.mainViewModel = mainViewModel
            it.lifecycleOwner = this
        }

        observe()

        navController =
            (supportFragmentManager.findFragmentById(R.id.activity_main_mainFCV) as NavHostFragment).navController

        mainViewModel.isLanguageLLGone(true)

        NetworkAvailability(applicationContext).registerNetworkCallback()

    }

    private fun checkLanguage() {

        val sharedPreference =
            getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val languageCode = sharedPreference.getString(LANGUAGE_CODE_KEY, "nope")

        if (languageCode != null) {

            if (languageCode != "nope") {
                appLanguage = languageCode
                changeLanguage(languageCode)
            }

        }

    }

    private fun changeLanguage(languageCode: String) {
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(languageCode))
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, displayMetrics)
    }

    private fun observe() {

        with(mainViewModel) {

            languageLLClick.observe(this@MainActivity, EventObserver {

                if (it) {
                    openLanguageDialog()
                }

            })

        }

    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun openLanguageDialog() {

        val dialogLanguage = MaterialAlertDialogBuilder(this, R.style.StyleDialog)

        val binding = DialogLanguageBinding.inflate(LayoutInflater.from(applicationContext))

        binding.let {
            it.mainViewModel = mainViewModel
            it.lifecycleOwner = this
        }

        val layoutLocalizationMainRV =
            binding.dialogLanguageMainRV

        val primaryLanguageList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.primaryLanguage)))
        val secondaryLanguageList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.secondaryLanguage)))

        val languageList = ArrayList<Language>()

        val sharedPreference =
            getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val language = sharedPreference.getString(LANGUAGE_KEY, "English")

        for (i in 0 until primaryLanguageList.size) {

            val checkedLanguage = primaryLanguageList.indexOf(language)

            if (i == checkedLanguage) {
                languageList.add(
                    Language(
                        primaryLanguageList[i],
                        secondaryLanguageList[i],
                        true
                    )
                )
            } else {
                languageList.add(
                    Language(
                        primaryLanguageList[i],
                        secondaryLanguageList[i],
                        false
                    )
                )
            }

        }

        mainViewModel.fillLanguageList(languageList)

        adapter = mainViewModel.languageList.value?.let { LanguageAdapter(it, this) }!!

        layoutLocalizationMainRV.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }

        mainViewModel.searchETText.observe(this, {

            if (it.isNullOrEmpty()) {
                binding.dialogLanguageMainET.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
                )
            } else {
                binding.dialogLanguageMainET.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_close,
                    0
                )
            }

            adapter.filter.filter(it)

        })

        binding.dialogLanguageMainET.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {

                if (binding.dialogLanguageMainET.compoundDrawables[2] != null) {

                    if (event.rawX >= binding.dialogLanguageMainET.right - binding.dialogLanguageMainET.compoundDrawables[2].bounds.width()
                    ) {

                        binding.dialogLanguageMainET.text.clear()

                        return@OnTouchListener true

                    }

                }

            }

            false

        })

        with(dialogLanguage) {
            setView(binding.root)
            show()
        }

    }

    override fun onItemClick(language: Language) {

        when (language.primaryLanguage) {
            "English" -> saveLanguage("en", "English")
            "Türkçe" -> saveLanguage("tr", "Türkçe")
            "Azəricə" -> saveLanguage("az", "Azəricə")
        }

    }

    private fun saveLanguage(languageCode: String, language: String) {

        val sharedPreference =
            getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val editor = sharedPreference!!.edit()

        with(editor) {
            putString(LANGUAGE_CODE_KEY, languageCode)
            putString(LANGUAGE_KEY, language)
            apply()
        }

        restart()

    }

    private fun restart() {
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        when (destination.label.toString()) {
            "SplashFragment" -> mainViewModel.isLanguageLLGone(true)
            "LogInFragment" -> mainViewModel.isLanguageLLGone(false)
            "SignUpFragment" -> mainViewModel.isLanguageLLGone(false)
            "SignUpUsernameFragment" -> mainViewModel.isLanguageLLGone(true)
            "FeedFragment" -> mainViewModel.isLanguageLLGone(true)
            "GetHelpLoggingInFragment" -> mainViewModel.isLanguageLLGone(true)
        }

    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(this)
    }

}
