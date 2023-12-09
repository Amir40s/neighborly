package com.technogenis.neighborlly.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.adapter.EventAdapter
import com.technogenis.neighborlly.adapter.FeedAdapter
import com.technogenis.neighborlly.databinding.ActivityMyEventsBinding
import com.technogenis.neighborlly.model.EventModel
import com.technogenis.neighborlly.model.FeedModel

class MyEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyEventsBinding

    lateinit var adapter : EventAdapter
    private lateinit var eventsList : ArrayList<EventModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var userUID : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.includeTopLayout.backText.text = "My Events"

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this,AddEventActivity::class.java)
            startActivity(intent)
        }

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)

        eventsList = arrayListOf<EventModel>()
        adapter = EventAdapter(eventsList,this,"my")
        binding.recycleView.adapter = adapter
        getFirestoreData()

    }

    private fun getFirestoreData() {
        firestore.collection("events")
            .whereEqualTo("userUID",userUID)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?)
                {
                    if (error!=null)
                    {
                        Log.e("Firestore Error", "onEvent: ${error.message.toString()}" )
                        return
                    }
                    for (snapShot : DocumentChange in value?.documentChanges!!)
                    {
                        if (snapShot.type == DocumentChange.Type.ADDED)
                        {
                            eventsList.add(snapShot.document.toObject(EventModel::class.java))
                        }
                    }

                    if (eventsList.size <=0)
                    {
                        binding.llNoResult.visibility = View.VISIBLE
                    }else{
                        binding.llNoResult.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }


            })
    }
}