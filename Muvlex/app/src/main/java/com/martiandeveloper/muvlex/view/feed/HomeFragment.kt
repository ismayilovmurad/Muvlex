package com.martiandeveloper.muvlex.view.feed

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.adapter.HomePostAdapter
import com.martiandeveloper.muvlex.databinding.FragmentHomeBinding
import com.martiandeveloper.muvlex.model.HomePost
import com.martiandeveloper.muvlex.viewmodel.feed.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import java.io.File


class HomeFragment : Fragment(), HomePostAdapter.ItemClickListener {

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: HomePostAdapter

    var fileName = ""

    lateinit var imageUri:Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        observe()

        viewModel.getFollowingList()

        binding.image.setOnClickListener {
            openGallery()
        }

        binding.button.setOnClickListener {
            FirebaseStorage.getInstance().reference.child(Firebase.auth.currentUser!!.uid).putFile(imageUri).addOnSuccessListener {
                Timber.d(it.storage.downloadUrl.toString())
            }

        }

        binding.button2.setOnClickListener {

            FirebaseStorage.getInstance().reference.child("${Firebase.auth.currentUser!!.uid}").downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).into(binding.image)
            }

        }

        return binding.root

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

            val selectedImage: Uri = data.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor: Cursor = requireContext().getContentResolver().query(
                selectedImage,
                filePathColumn, null, null, null
            )!!
            cursor.moveToFirst()

            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor.close()

            //binding.image.setImageBitmap(BitmapFactory.decodeFile(picturePath))

            imageUri = data.data!!
            Glide.with(this).load(data.data).into(binding.image)
        }
    }

    private fun observe() {

        viewModel.followingList.observe(viewLifecycleOwner, {
            setUpRecyclerView()
        })

    }

    private fun setUpRecyclerView() {

        adapter = HomePostAdapter(this)

        binding.fragmentHomeMainRV.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
            it.setHasFixedSize(true)
        }

        viewModel.getData(adapter)

    }

    override fun onItemClick(homePost: HomePost) {
        Timber.d(homePost.item_id)
    }

}
