package com.technogenis.neighborlly.chat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUID : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.uid.toString()

        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.includeTopLayout.backText.text = "Neighbor Chat"
    }
}