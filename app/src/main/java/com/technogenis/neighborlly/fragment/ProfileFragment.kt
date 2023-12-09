package com.technogenis.neighborlly.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.technogenis.neighborlly.databinding.FragmentProfileBinding
import java.util.UUID


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var imgUri : Uri
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userUID : String
    private lateinit var url : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(layoutInflater, container, false)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID = auth.currentUser?.uid.toString()

        getProfileData()

        binding.profileImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }


        return binding.root
    }

    private fun getProfileData() {
        firestore.collection("users").document(userUID)
            .get().addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    val document = task.result
                    binding.tvName.text = document.getString("fullName")
                    binding.tvEmail.text = document.getString("email")
                    binding.tvPhone.text = document.getString("phone")
                    binding.tvPassword.text = document.getString("password")
                    if (document.contains("profileImageUrl")){
                        activity?.let { Glide.with(it).load(document.getString("profileImageUrl")).into(binding.profileImage) }
                    }
                }else{
//                   loadingBar.HideDialog()
                }
            }
    }

    fun uploadImage(){
        if(imgUri!=null){
            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Uploading your image..")
            progressDialog.show()

            val ref : StorageReference = FirebaseStorage.getInstance().reference
                .child(UUID.randomUUID().toString())
            ref.putFile(imgUri).addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                   url = it.toString()
                    updateImageToDatabase()
                }


            }.addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(activity, "Fail to Upload Image..", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun updateImageToDatabase() {
        val map = hashMapOf<String,Any>(
         "profileImageUrl" to url
        )
        firestore.collection("users").document(userUID)
            .update(map).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(activity, "profile image updated...", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener{
                Toast.makeText(activity, "something went wrong!!", Toast.LENGTH_SHORT)
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
                binding.profileImage.setImageURI(imgUri)
                uploadImage()
            }
        }
}