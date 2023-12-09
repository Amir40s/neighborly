package com.technogenis.neighborlly.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.activity.MyEventsActivity
import com.technogenis.neighborlly.activity.MyFeedsActivity
import com.technogenis.neighborlly.databinding.FragmentHomeBinding
import com.technogenis.neighborlly.menuActivity.AddFeedActivity


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUID : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.uid.toString()

        // functions
        imageSlider()

        binding.llFeed.setOnClickListener {
            val intent = Intent(activity,MyFeedsActivity::class.java)
            startActivity(intent)
        }

        binding.llEvents.setOnClickListener {
            val intent = Intent(activity,MyEventsActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun imageSlider() {
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://www.neighbourhoodnetworks.org/wp-content/uploads/2022/05/cropped-NN-Logo-PNG.png","Neighborhood Network"))
        imageList.add(SlideModel("https://www.mnn.org/sites/default/files/2019-04/Slider_3_Firehouse_V1.jpg","Grow Your Network"))
        imageList.add(SlideModel("https://neighborhood-networks.org/wp-content/uploads/2020/12/Home_Network_Web.jpg","Meet With Neighbor"))
        imageList.add(SlideModel("https://es.statefarm.com/content/dam/sf-library/en-us/secure/legacy/simple-insights/how-to-choose-the-right-neighborhood.jpg","Connect With Us"))

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }


}