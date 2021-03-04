package com.martiandeveloper.muvlex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewUserItemBinding
import com.martiandeveloper.muvlex.model.User


class UserPostAdapter__(private val itemCLickListener: ItemClickListener) :
    PagingDataAdapter<User, UserPostAdapter__.UserListViewHolder>(UserListDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
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

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(context, getItem(position), itemCLickListener)
    }

    class UserListDiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
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

                    /*with(user) {

                        it.title =
                            if (user.username != null) username else context.resources.getString(R.string.unknown)

                        itemView.setOnClickListener {
                            itemClickListener.onItemClick(this)
                        }

                    }*/

                    it.executePendingBindings()
                }

        }

    }

    interface ItemClickListener {
        fun onItemClick(UserList: User)
    }

}
