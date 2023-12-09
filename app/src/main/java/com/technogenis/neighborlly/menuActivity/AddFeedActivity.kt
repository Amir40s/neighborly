package com.technogenis.neighborlly.menuActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityAddFeedBinding
import java.util.UUID

class AddFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFeedBinding
    private lateinit var title : String
    private lateinit var desc : String
    private lateinit var url : String
    private  var name : String = ""
    private  var profileUrl : String = "no"

    private lateinit var imgUri : Uri

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUID : String

    private var loadingBar = LoadingBar(this)
    private var  currentDateTime = CurrentDateTime(this)



    override fun onStart() {
        super.onStart()
        getUserData()
    }



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.uid.toString()

        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.includeTopLayout.backText.text = "Add New Feed"

        binding.feedImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }

        binding.btnPublish.setOnClickListener {
            title = binding.edTitle.text.toString()
            desc = binding.edDesc.text.toString()

            if (isValid(title,desc,imgUri)){
                saveFeedImage()
            }
        }
    }

    private fun getUserData() {
        firebaseFirestore.collection("users").document(userUID)
            .get().addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    val document = task.result
                   name = document.getString("fullName").toString()
                    if (document.contains("profileImageUrl")){
                        profileUrl = document.getString("profileImageUrl").toString()
                    }
                }else{
//                   loadingBar.HideDialog()
                }
            }
    }

    private fun saveFeedImage() {
        loadingBar.ShowDialog("please wait")
        val ref : StorageReference = FirebaseStorage.getInstance().reference
            .child(UUID.randomUUID().toString())
        ref.putFile(imgUri).addOnSuccessListener { taskSnapshot ->

            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                url = it.toString()
                uploadData()
            }


        }.addOnFailureListener{
            Toast.makeText(this, "Fail to Upload Image..", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun uploadData() {

        val id = firebaseFirestore.collection("feeds").document().id
        val map = hashMapOf<String,Any>(
            "title" to title,
            "desc" to desc,
            "url" to url,
            "feedID" to id,
            "userUID" to userUID,
            "name" to name,
            "date" to currentDateTime.getCurrentDate().toString(),
            "time" to currentDateTime.getTimeWithAmPm().toString(),
            "profileUrl" to profileUrl,
        )

        firebaseFirestore.collection("feeds").document(id).set(map)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    loadingBar.HideDialog()
                    Toast.makeText(this, "Feed Publish completed...", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                loadingBar.HideDialog()
                Toast.makeText(this, "failed to  Publish...", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                imgUri = data?.data!!
                binding.feedImage.setImageURI(imgUri)
            }
        }

    private fun isValid(title: String, desc: String, imgUri: Uri): Boolean {

        if (title.isEmpty()){
            binding.edTitle.error = "title required"
            binding.edTitle.requestFocus()
            return false
        }

        if (desc.isEmpty()){
            binding.edDesc.error = "desc required"
            binding.edDesc.requestFocus()
            return false
        }
        if (imgUri == null){
           Toast.makeText(this,"image required",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}