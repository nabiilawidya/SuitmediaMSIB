package com.nabiilawidya.suitmediamsib.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nabiilawidya.suitmediamsib.databinding.ItemUserBinding

class UserAdapter(
    private val users: MutableList<DataItem> = mutableListOf(),
    private var onItemClickCallback: OnItemClickCallback? = null
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItem: DataItem) {
            binding.apply {
                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(dataItem)
                }
                tvFirstName.text = dataItem.firstName
                tvLastName.text = dataItem.lastName
                tvEmailUser.text = dataItem.email
                Glide.with(itemView.context)
                    .load(dataItem.avatar)
                    .circleCrop()
                    .into(ciUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataItem)

    }

    fun setClickCallback(itemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = itemClickCallback
    }

    fun clearUsers() {
        this.users.clear()
        notifyDataSetChanged()
    }

    fun setList(newUsers: List<DataItem>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }
}
