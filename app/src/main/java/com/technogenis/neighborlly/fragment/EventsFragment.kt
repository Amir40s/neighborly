package com.technogenis.neighborlly.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.technogenis.neighborlly.databinding.FragmentEventsBinding
import com.technogenis.neighborlly.databinding.FragmentFeedsBinding
import com.technogenis.neighborlly.model.EventModel
import com.technogenis.neighborlly.model.FeedModel


class EventsFragment : Fragment() {

    private lateinit var binding: FragmentEventsBinding

    lateinit var adapter : EventAdapter
    private lateinit var eventsList : ArrayList<EventModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var userUID : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentEventsBinding.inflate(layoutInflater, container, false)

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        binding.recycleView.layoutManager = LinearLayoutManager(activity)
        binding.recycleView.setHasFixedSize(true)

        eventsList = arrayListOf<EventModel>()
        adapter = activity?.let { EventAdapter(eventsList, it,"fragment") }!!
        binding.recycleView.adapter = adapter
        getFirestoreData()

        return binding.root
    }
    private fun getFirestoreData() {
        firestore.collection("events")
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