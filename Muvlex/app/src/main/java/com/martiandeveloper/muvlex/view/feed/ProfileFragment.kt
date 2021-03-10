package com.martiandeveloper.muvlex.view.feed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.LanguageAdapter
import com.martiandeveloper.muvlex.adapter.UserPostAdapter
import com.martiandeveloper.muvlex.databinding.DialogLanguageProfileBinding
import com.martiandeveloper.muvlex.databinding.FragmentProfileBinding
import com.martiandeveloper.muvlex.model.Language
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.model.Post
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.view.main.MainActivity
import com.martiandeveloper.muvlex.view.main.PrivacyPolicyActivity
import com.martiandeveloper.muvlex.viewmodel.feed.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), UserPostAdapter.ItemClickListener,
    LanguageAdapter.ItemClickListener {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var binding: FragmentProfileBinding

    private lateinit var adapter: UserPostAdapter

    private lateinit var languageAdapter: LanguageAdapter

    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setToolbar()

        setHasOptionsMenu(true)

        observe()

        setViewData()

        setRecyclerView()

        dialog = context?.let { MaterialAlertDialogBuilder(it, R.style.StyleDialog).create() }!!

        Firebase.firestore.collection("posts").document().addSnapshotListener { value, _ ->
            if (value != null && value.exists()) if (Firebase.auth.currentUser != null) viewModel.getData(
                Firebase.auth.currentUser!!.uid,
                adapter
            )
        }

        return binding.root

    }

    private fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.fragmentProfileMainMTB)

        if ((activity as AppCompatActivity).supportActionBar != null) (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(
            false
        )
    }

    private fun observe() {

        with(viewModel) {

            editMBTNClick.observe(viewLifecycleOwner, {
                startActivity(Intent(context, EditProfileActivity::class.java))
            })

        }

    }

    private fun setViewData() {

        with(Firebase.auth.currentUser) {

            if (this != null)

                Firebase.firestore.collection("users").document(this.uid)
                    .addSnapshotListener { value, _ ->

                        if (value != null && value.exists()) {

                            viewModel.setTitle(value.get("username").toString())
                            viewModel.setFollowing((value.get("following") as ArrayList<*>).size.toString())
                            viewModel.setFollowers((value.get("followers") as ArrayList<*>).size.toString())
                            viewModel.setBio(value.get("bio").toString())

                        }

                    }

        }

    }

    override fun onResume() {

        super.onResume()

        context?.let {

            FirebaseStorage.getInstance().reference.child("user_profile_picture")
                .child(Firebase.auth.currentUser!!.uid).downloadUrl.addOnSuccessListener {
                binding.fragmentProfilePosterIV.loadWithUri(requireContext(), it)
            }.addOnFailureListener {
                binding.fragmentProfilePosterIV.load(requireContext(), null)
            }

        }

    }

    private fun setRecyclerView() {

        adapter = UserPostAdapter(this)

        binding.fragmentProfileMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        CoroutineScope(Dispatchers.Main).launch {

            adapter.loadStateFlow.collectLatest {

                viewModel.setPosts(adapter.itemCount.toString())

                if (it.refresh is LoadState.NotLoading) if (adapter.itemCount > 0) viewModel.isYourPostsWillAppearHereLLGone(
                    true
                )

            }

        }

        if (Firebase.auth.currentUser != null) viewModel.getData(
            Firebase.auth.currentUser!!.uid,
            adapter
        )

    }

    override fun onItemClick(post: Post) {
        val movie = Movie(
            false,
            null,
            post.genreIds,
            post.id,
            post.originalLanguage,
            post.originalTitle,
            post.overview,
            null,
            post.posterPath,
            post.releaseDate,
            post.title,
            false,
            post.voteAverage?.toDouble(),
            null
        )

        startActivity(Intent(context, MovieDetailActivity::class.java).putExtra("movie", movie))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.activeLanguage -> {
                openLanguageDialog()
                true
            }
            R.id.privacyPolicy -> {
                startActivity(Intent(context, PrivacyPolicyActivity::class.java))
                true
            }
            R.id.signOut -> {
                Firebase.auth.signOut()
                restart()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun openLanguageDialog() {

        val binding = DialogLanguageProfileBinding.inflate(LayoutInflater.from(context))

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        val primaryLanguageList: java.util.ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.primaryLanguage)))
        val secondaryLanguageList: java.util.ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.secondaryLanguage)))

        val languageList = java.util.ArrayList<Language>()

        for (i in 0 until primaryLanguageList.size) {

            languageList.add(
                Language(
                    primaryLanguageList[i],
                    secondaryLanguageList[i],
                    i == primaryLanguageList.indexOf(
                        context?.getSharedPreferences(
                            LANGUAGE_SHARED_PREFERENCE,
                            Context.MODE_PRIVATE
                        )?.getString(LANGUAGE_KEY, "English")
                    )
                )
            )

        }

        viewModel.fillLanguageList(languageList)

        languageAdapter = viewModel.languageList.value?.let { LanguageAdapter(it, this) }!!

        binding.dialogLanguageProfileMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = languageAdapter
            it.setHasFixedSize(true)
        }

        viewModel.searchETText.observe(this, {
            binding.dialogLanguageProfileMainET.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it.isNullOrEmpty()) 0 else R.drawable.ic_close,
                0
            )

            languageAdapter.filter.filter(it)
        })

        binding.dialogLanguageProfileMainET.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP)
                if (binding.dialogLanguageProfileMainET.compoundDrawables[2] != null)
                    if (event.rawX >= binding.dialogLanguageProfileMainET.right - binding.dialogLanguageProfileMainET.compoundDrawables[2].bounds.width()
                    ) {
                        binding.dialogLanguageProfileMainET.text.clear()

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
            ENGLISH -> saveLanguage(ENGLISH_CODE, ENGLISH)
            TURKISH -> saveLanguage(TURKISH_CODE, TURKISH)
            AZERBAIJANI -> saveLanguage(AZERBAIJANI_CODE, AZERBAIJANI)
        }

    }

    private fun saveLanguage(languageCode: String, language: String) {

        with(
            context?.getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE)!!.edit()
        ) {
            putString(LANGUAGE_CODE_KEY, languageCode)
            putString(LANGUAGE_KEY, language)
            apply()
        }

        restart()

    }

    private fun restart() {
        startActivity(
            Intent(
                activity,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        activity?.finish()
    }

}
