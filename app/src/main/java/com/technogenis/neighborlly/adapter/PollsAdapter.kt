package com.technogenis.neighborlly.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.chat.ChatActivity
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
        holder.btnChat.setOnClickListener {
            context.startActivity(Intent(context,ChatActivity::class.java))
        }

        holder.btnChat.setOnClickListener {
            try {
                var text = "Hello! how are you"
                //           String toNumber = "xxxxxxxxxx"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://api.whatsapp.com/send?phone=${users.phone}&text=$text")
                context.startActivity(intent)
            }catch (_: Exception){

            }
        }
    }

    override fun getItemCount(): Int {
       return userList.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val profileImage : ImageView = itemView.findViewById(R.id.profile_image)
        val name : TextView = itemView.findViewById(R.id.tv_name)
        val btnChat : Button = itemView.findViewById(R.id.btn_chat)
    }
}