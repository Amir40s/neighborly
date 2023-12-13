package com.technogenis.neighborlly.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.FragmentFAQBinding


class FAQFragment : Fragment() {

    private lateinit var binding : FragmentFAQBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentFAQBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


}