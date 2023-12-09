package com.technogenis.neighborlly.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeTopLayout.backText.text = "Feeds Details"
        binding.includeTopLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        if (intent.getStringExtra("profileUrl").toString() != "no"){
            Glide.with(this).load(intent.getStringExtra("profileUrl").toString()).into(binding.profileImage)
        }
        binding.tvName.text = intent.getStringExtra("name").toString()
        binding.tvDateTime.text = intent.getStringExtra("dateTime").toString()
        binding.tvTitle.text = intent.getStringExtra("title").toString()
        binding.tvDesc.text = intent.getStringExtra("desc").toString()
        Glide.with(this).load(intent.getStringExtra("url").toString()).into(binding.feedImage)




    }
}