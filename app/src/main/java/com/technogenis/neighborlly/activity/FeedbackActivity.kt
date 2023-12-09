package com.technogenis.neighborlly.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var message : String


    private var loadingBar = LoadingBar(this)
    private var currentDateTime =  CurrentDateTime(this)

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.uid.toString()

        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.includeTopLayout.backText.text = "Feedbacks"

        binding.btnPublish.setOnClickListener {
            loadingBar.ShowDialog("please wait...")
            message = binding.edDesc.text.toString()
            if (message.isEmpty()){
                binding.edDesc.error = "message required"
                binding.edDesc.requestFocus()
                loadingBar.HideDialog()
            }else{
                saveFeedback()
            }
        }

    }

    private fun saveFeedback() {
        val id = firebaseFirestore.collection("feedback").document().id
        val map = hashMapOf<String,Any>(
            "message" to message,
            "id" to id,
            "date" to currentDateTime.getCurrentDate().toString(),
            "time" to currentDateTime.getTimeWithAmPm().toString(),
        )
       firebaseFirestore.collection("feedback").document(id).set(map)
           .addOnCompleteListener {
               if (it.isSuccessful){
                   loadingBar.HideDialog()
                   Toast.makeText(this,"feedback submitted...",Toast.LENGTH_SHORT).show()
               }
           }.addOnFailureListener{
               loadingBar.HideDialog()
               Toast.makeText(this,"something went wrong...",Toast.LENGTH_SHORT).show()
           }

    }
}