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

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: LanguageAdapter

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        changeLanguage()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        observe()

        navController =
            (supportFragmentManager.findFragmentById(R.id.activity_main_mainFCV) as NavHostFragment).navController

        NetworkAvailability(applicationContext).registerNetworkCallback()

    }

    private fun changeLanguage() {
        appLanguage =
            getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(
                LANGUAGE_CODE_KEY,
                "en"
            )!!

        val configuration = resources.configuration
        configuration.setLocale(Locale(appLanguage))
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun observe() {

        viewModel.languageLLClick.observe(this@MainActivity, EventObserver {
            if (it) openLanguageDialog()
        })

    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun openLanguageDialog() {

        val dialog = MaterialAlertDialogBuilder(this, R.style.StyleDialog)

        val binding = DialogLanguageBinding.inflate(LayoutInflater.from(applicationContext))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        val primaryLanguageList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.primaryLanguage)))
        val secondaryLanguageList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.secondaryLanguage)))

        val languageList = ArrayList<Language>()

        for (i in 0 until primaryLanguageList.size) {

            languageList.add(
                Language(
                    primaryLanguageList[i],
                    secondaryLanguageList[i],
                    i == primaryLanguageList.indexOf(
                        getSharedPreferences(
                            LANGUAGE_SHARED_PREFERENCE,
                            Context.MODE_PRIVATE
                        ).getString(LANGUAGE_KEY, "English")
                    )
                )
            )

        }

        viewModel.fillLanguageList(languageList)

        adapter = viewModel.languageList.value?.let { LanguageAdapter(it, this) }!!

        binding.dialogLanguageMainRV.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }

        viewModel.searchETText.observe(this, {
            binding.dialogLanguageMainET.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it.isNullOrEmpty()) 0 else R.drawable.ic_close,
                0
            )

            adapter.filter.filter(it)
        })

        binding.dialogLanguageMainET.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP)
                if (binding.dialogLanguageMainET.compoundDrawables[2] != null)
                    if (event.rawX >= binding.dialogLanguageMainET.right - binding.dialogLanguageMainET.compoundDrawables[2].bounds.width()
                    ) {
                        binding.dialogLanguageMainET.text.clear()

                        return@OnTouchListener true
                    }

            false
        })

        with(dialog) {
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

        with(getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE)!!.edit()) {
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
        viewModel.isLanguageLLGone(!(destination.label.toString() == "LogInFragment" || destination.label.toString() == "SignUpFragment"))
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
