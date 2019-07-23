package com.eemanapp.fuoexaet.view.prep


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentSliderBinding

import com.eemanapp.fuoexaet.di.Injectable
import com.google.android.material.tabs.TabLayout
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener


/**
 * A simple [Fragment] subclass.
 */
class CarouselSliderFragment : Fragment(), Injectable, HomeScreenInitializer {

    override fun initializeHomeScreen(view:View) {
        when (view.id){
            R.id.btn_login ->{
                findNavController().navigate(R.id.to_loginFragment)
            }

            R.id.btn_signup -> {
                findNavController().navigate(R.id.to_signupFragment)
            }
        }
    }

    private var slideViews = intArrayOf(R.layout.carousel_slide1, R.layout.carousel_slider2)
    private lateinit var binding:FragmentSliderBinding
    private var mPagerAdapter: SliderViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_slider, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPagerAdapter = SliderViewPager(context!!, slideViews, this)
        binding.viewPager.adapter = mPagerAdapter
        binding.layoutDots.setupWithViewPager(binding.viewPager, true)
    }
}
