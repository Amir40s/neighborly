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
import com.technogenis.neighborlly.adapter.FeedAdapter
import com.technogenis.neighborlly.databinding.ActivityMyFeedsBinding
import com.technogenis.neighborlly.menuActivity.AddFeedActivity
import com.technogenis.neighborlly.model.FeedModel

class MyFeedsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyFeedsBinding
    lateinit var adapter : FeedAdapter
    private lateinit var feedsList : ArrayList<FeedModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var userUID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFeedsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.includeTopLayout.backText.text = "My Feeds"

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this,AddFeedActivity::class.java)
            startActivity(intent)
        }

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)

        feedsList = arrayListOf<FeedModel>()
        adapter = FeedAdapter(feedsList,this,"my")
        binding.recycleView.adapter = adapter
        getFirestoreData()
    }
    private fun getFirestoreData() {
        firestore.collection("feeds")
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
                            feedsList.add(snapShot.document.toObject(FeedModel::class.java))
                        }
                    }

                    if (feedsList.size <=0)
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