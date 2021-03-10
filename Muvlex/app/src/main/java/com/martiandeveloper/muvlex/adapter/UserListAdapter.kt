package com.martiandeveloper.muvlex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewUserItemBinding
import com.martiandeveloper.muvlex.model.User
import com.martiandeveloper.muvlex.utils.check
import com.martiandeveloper.muvlex.utils.load
import com.martiandeveloper.muvlex.utils.loadWithUri

class UserListAdapter(
    private val itemCLickListener: ItemClickListener
) : PagingDataAdapter<User, RecyclerView.ViewHolder>(UserDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return UserListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.recyclerview_user_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserListViewHolder).bind(context, getItem(position), itemCLickListener)
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    class UserListViewHolder(private val binding: RecyclerviewUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            user: User?,
            itemClickListener: ItemClickListener
        ) {
            if (user != null)

                binding.let {

                    with(user) {

                        it.username =
                            if (username.check()) username else context.resources.getString(
                                R.string.unknown
                            )

                        it.bio =
                            if (bio.check()) bio else context.resources.getString(
                                R.string.unknown
                            )

                        if (uid.check())
                            FirebaseStorage.getInstance().reference.child("user_profile_picture")
                                .child(uid!!).downloadUrl.addOnSuccessListener { it1 ->
                                    it.recyclerviewMovieItemPosterIV.loadWithUri(context, it1)
                                }.addOnFailureListener { _ ->
                                    it.recyclerviewMovieItemPosterIV.load(context, null)
                                }

                        itemView.setOnClickListener {
                            itemClickListener.onItemClick(this)
                        }

                    }

                    it.executePendingBindings()

                }

        }

    }

    interface ItemClickListener {
        fun onItemClick(user: User)
    }

}
