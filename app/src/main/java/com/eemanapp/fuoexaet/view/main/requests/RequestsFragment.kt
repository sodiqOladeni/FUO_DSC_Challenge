package com.eemanapp.fuoexaet.view.main.requests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.eemanapp.fuoexaet.R
import com.google.android.material.tabs.TabLayout

class RequestsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), childFragmentManager)
        val viewPager: ViewPager = requireView().findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = requireView().findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}
