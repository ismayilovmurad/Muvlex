package com.martiandeveloper.muvlex.view.feed

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.FragmentEditProfileBinding
import com.martiandeveloper.muvlex.utils.EventObserver
import com.martiandeveloper.muvlex.viewmodel.feed.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: EditProfileViewModel

    private lateinit var binding: FragmentEditProfileBinding

    var fileName = ""

    lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setToolbar()

        setHasOptionsMenu(true)

        observe()

        return binding.root

    }

    private fun setToolbar() {

        with(activity as AppCompatActivity) {

            setSupportActionBar(binding.fragmentPrivacyPolicyMainMTB)

            if (supportActionBar != null) with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }

        }

    }

    private fun observe() {

        with(viewModel) {

            pictureIVClick.observe(viewLifecycleOwner, EventObserver {
                openGallery()
            })

            saveMBTNClick.observe(viewLifecycleOwner, EventObserver {
                FirebaseStorage.getInstance().reference.child(Firebase.auth.currentUser!!.uid)
                    .putFile(imageUri).addOnSuccessListener {
                        findNavController().navigateUp()
                    }
            })

        }

    }

    private fun openGallery() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 102)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 102)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            Glide.with(this).load(data.data).into(binding.fragmentEditProfilePosterIV)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

}
