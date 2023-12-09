package com.technogenis.neighborlly.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityAddEventBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddEventActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityAddEventBinding

    private lateinit var date : String
    private lateinit var time : String
    private lateinit var title : String
    private lateinit var desc : String
    private lateinit var location : String
    private  var eventImageUrl : String = "no"
    private  var name : String = ""
    private  var profileUrl : String = "no"

    private lateinit var imgUri : Uri

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUID : String

    private var loadingBar = LoadingBar(this)
    private var currentDateTime =  CurrentDateTime(this)

    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("hh:mm a", Locale.US)

    override fun onStart() {
        super.onStart()
        setCurrentDateTime()
        getUserData()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.uid.toString()

        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.includeTopLayout.backText.text = "Add New Events"

        binding.tvDate.setOnClickListener {
            showDateDialog(binding.tvDate)
        }

        binding.tvTime.setOnClickListener {
            TimePickerDialog(
                this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.eventImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }

        binding.btnPublish.setOnClickListener {
            title = binding.edTitle.text.toString()
            desc = binding.edDesc.text.toString()
            location = binding.edLocation.text.toString()

            if (isValid(title,desc,location,imgUri)){
                saveEventImage()
            }
        }

    }

    private fun saveEventImage() {
        loadingBar.ShowDialog("please wait")
        val ref : StorageReference = FirebaseStorage.getInstance().reference
            .child(UUID.randomUUID().toString())
        ref.putFile(imgUri).addOnSuccessListener { taskSnapshot ->

            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                eventImageUrl = it.toString()
                uploadData()
            }


        }.addOnFailureListener{
            Toast.makeText(this, "Fail to Upload Image..", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadData() {
        val id = firebaseFirestore.collection("events").document().id
        val map = hashMapOf<String,Any>(
            "title" to title,
            "desc" to desc,
            "url" to eventImageUrl,
            "eventID" to id,
            "userUID" to userUID,
            "name" to name,
            "date" to date,
            "time" to time,
            "location" to location,
            "profileUrl" to profileUrl,
        )

        firebaseFirestore.collection("events").document(id).set(map)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    loadingBar.HideDialog()
                    Toast.makeText(this, "Event Publish completed...", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                loadingBar.HideDialog()
                Toast.makeText(this, "failed to  Publish...", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun isValid(title: String, desc: String,location: String, imgUri: Uri): Boolean {

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

        if (location.isEmpty()){
            binding.edLocation.error = "location required"
            binding.edLocation.requestFocus()
            return false
        }
        if (imgUri == null){
            Toast.makeText(this,"image required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                imgUri = data?.data!!
                binding.eventImage.setImageURI(imgUri)
            }
        }

    private fun setCurrentDateTime() {
        date = currentDateTime.getCurrentDate().toString()
        time = currentDateTime.getTimeWithAmPm().toString()
        binding.tvDate.text = date
        binding.tvTime.text = time
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDateDialog(tv_date: TextView) {
        val calendar = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val simpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")
                tv_date.text = simpleDateFormat.format(calendar.time)
                date = tv_date.text.toString()

            }
        DatePickerDialog(
            this, dateSetListener,
            calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        ).show()
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

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY,hourOfDay)
            set(Calendar.MINUTE,minute)
        }
        displayFormatTime(calendar.timeInMillis)
    }
    private fun displayFormatTime(timeStamp : Long){
        binding.tvTime.text = formatter.format(timeStamp)
    }
}