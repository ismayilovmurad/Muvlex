package com.martiandeveloper.muvlex.view.feed

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.ActivityEditProfileBinding
import com.martiandeveloper.muvlex.utils.*
import com.martiandeveloper.muvlex.viewmodel.feed.EditProfileViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: EditProfileViewModel

    private lateinit var binding: ActivityEditProfileBinding

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        setToolbar()

        observe()

        with(Firebase.auth.currentUser) {

            if (this != null)

                FirebaseStorage.getInstance().reference.child("user_profile_picture").child(this.uid).downloadUrl.addOnSuccessListener {
                    Glide.with(this@EditProfileActivity).load(it)
                        .into(binding.activityEditProfilePosterIV)
                }.addOnFailureListener {
                    binding.activityEditProfilePosterIV.load(this@EditProfileActivity, null)
                }

            Firebase.firestore.collection("users").document(this!!.uid)
                .addSnapshotListener { value, _ ->
                    if (value != null && value.exists()) binding.activityEditProfileBioET.setText(
                        value.get("bio").toString()
                    )
                }
        }

        binding.activityEditProfileMainPB.visibility = View.GONE

    }

    private fun setToolbar() {

        setSupportActionBar(binding.activityEditProfileMainMTB)

        if (supportActionBar != null)

            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }

    }

    private fun observe() {

        with(viewModel) {

            pictureIVClick.observe(this@EditProfileActivity, EventObserver {
                openGallery()
            })

            saveMBTNClick.observe(this@EditProfileActivity, EventObserver {
                if (networkAvailable) {
                    binding.activityEditProfileSaveMBTN.disable(this@EditProfileActivity)
                    binding.activityEditProfileMainPB.visibility = View.VISIBLE
                    save(imageUri)
                } else R.string.no_internet_connection.showToast(
                    this@EditProfileActivity
                )
            })

            complete.observe(this@EditProfileActivity, {
                onBackPressed()
            })

        }

    }

    private fun openGallery() {
        if (this.let {
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
            Glide.with(this).load(data.data).into(binding.activityEditProfilePosterIV)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

}
