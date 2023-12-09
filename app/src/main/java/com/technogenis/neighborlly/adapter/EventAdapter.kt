package com.technogenis.neighborlly.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.technogenis.neighborlly.model.EventModel

class EventAdapter(private val eventList : ArrayList<EventModel>, val context: Context, val activityName : String) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_list_layout,parent,false)

        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {

        val events = eventList[position]
        if (activityName == "my"){
            holder.deleteImage.visibility = View.VISIBLE
        }else {
            holder.deleteImage.visibility = View.GONE
        }



        if (events.profileUrl != "no"){
            Glide.with(context).load(events.profileUrl).into(holder.profileImage)
        }
        holder.name.text = events.name
        holder.location.text = events.location
        holder.date.text = "${events.date}"
        holder.time.text = "${events.time}"
        holder.title.text = events.title
        holder.desc.text = events.desc
        Glide.with(context).load(events.url).into(holder.eventImage)


        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetailsActivity::class.java)
            intent.putExtra("name",events.name)
            intent.putExtra("dateTime","${events.date} ${events.time}")
            intent.putExtra("title",events.title)
            intent.putExtra("desc",events.desc)
            intent.putExtra("url",events.url)
            intent.putExtra("profileUrl",events.profileUrl)
            context.startActivity(intent)
        }

        holder.deleteImage.setOnClickListener {
           FirebaseFirestore.getInstance().collection("events")
               .document(events.eventID.toString()).delete().addOnCompleteListener {
                   if (it.isSuccessful){
                       val intent = Intent(context,MyFeedsActivity::class.java)
                       context.startActivity(intent)
                       Toast.makeText(context,"Event Delete Completed..",Toast.LENGTH_SHORT).show()
                   }
               }.addOnFailureListener{
                   Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
               }
        }


    }



    override fun getItemCount(): Int {
       return eventList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // view reference
        val date : TextView = itemView.findViewById(R.id.tv_date)
        val time : TextView = itemView.findViewById(R.id.tv_time)
        val location : TextView = itemView.findViewById(R.id.tv_location)
        val profileImage : ImageView = itemView.findViewById(R.id.profile_image)
        val eventImage : ImageView = itemView.findViewById(R.id.event_image)
        val deleteImage : ImageView = itemView.findViewById(R.id.delete_image)
        val name : TextView = itemView.findViewById(R.id.tv_name)
        val title : TextView = itemView.findViewById(R.id.tv_title)
        val desc : TextView = itemView.findViewById(R.id.tv_desc)

    }
}