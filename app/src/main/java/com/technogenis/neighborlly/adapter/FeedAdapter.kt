package com.technogenis.neighborlly.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.telecom.Call.Details
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.activity.DetailsActivity
import com.technogenis.neighborlly.activity.MyFeedsActivity
import com.technogenis.neighborlly.model.FeedModel

class FeedAdapter(private val feedList : ArrayList<FeedModel>,val context: Context,val activityName : String) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.feed_list_layout,parent,false)

        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FeedAdapter.ViewHolder, position: Int) {

        val feeds = feedList[position]
        if (activityName == "my"){
            holder.deleteImage.visibility = View.VISIBLE
        }else {
            holder.deleteImage.visibility = View.GONE
        }



        if (feeds.profileUrl != "no"){
            Glide.with(context).load(feeds.profileUrl).into(holder.profileImage)
        }
        holder.name.text = feeds.name
        holder.dateTime.text = "${feeds.date} ${feeds.time}"
        holder.title.text = feeds.title
        holder.desc.text = feeds.desc
        Glide.with(context).load(feeds.url).into(holder.feedImage)


        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetailsActivity::class.java)
            intent.putExtra("name",feeds.name)
            intent.putExtra("dateTime","${feeds.date} ${feeds.time}")
            intent.putExtra("title",feeds.title)
            intent.putExtra("desc",feeds.desc)
            intent.putExtra("url",feeds.url)
            intent.putExtra("profileUrl",feeds.profileUrl)
            context.startActivity(intent)
        }

        holder.deleteImage.setOnClickListener {
           FirebaseFirestore.getInstance().collection("feeds")
               .document(feeds.feedID.toString()).delete().addOnCompleteListener {
                   if (it.isSuccessful){
                       val intent = Intent(context,MyFeedsActivity::class.java)
                       context.startActivity(intent)
                       Toast.makeText(context,"Feed Delete Completed..",Toast.LENGTH_SHORT).show()
                   }
               }.addOnFailureListener{
                   Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
               }
        }


    }



    override fun getItemCount(): Int {
       return feedList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // view reference
        val dateTime : TextView = itemView.findViewById(R.id.tv_dateTime)
        val profileImage : ImageView = itemView.findViewById(R.id.profile_image)
        val feedImage : ImageView = itemView.findViewById(R.id.feed_image)
        val deleteImage : ImageView = itemView.findViewById(R.id.delete_image)
        val name : TextView = itemView.findViewById(R.id.tv_name)
        val title : TextView = itemView.findViewById(R.id.tv_title)
        val desc : TextView = itemView.findViewById(R.id.tv_desc)

    }
}