package com.technogenis.neighborlly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.model.UserModel

class PollsAdapter(private val userList : ArrayList<UserModel>,val context: Context) : RecyclerView.Adapter<PollsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.users_list_layout,parent,false)

        return ViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: PollsAdapter.ViewHolder, position: Int) {

        val users  = userList[position]
        if (users.profileImageUrl!="no"){
            Glide.with(context).load(users.profileImageUrl).into(holder.profileImage)
        }
        holder.name.text = users.fullName
    }

    override fun getItemCount(): Int {
       return userList.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val profileImage : ImageView = itemView.findViewById(R.id.profile_image)
        val name : TextView = itemView.findViewById(R.id.tv_name)
        val btnConnect : Button = itemView.findViewById(R.id.btn_connect)
    }
}