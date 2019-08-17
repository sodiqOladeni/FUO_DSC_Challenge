package com.eemanapp.fuoexaet.view.main.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.HomeDashboardFragmentBinding
import com.eemanapp.fuoexaet.viewModel.HomeDashboardViewModel

class HomeDashboardFragment : Fragment() {

    companion object {
        fun newInstance() = HomeDashboardFragment()
    }

    private lateinit var viewModel: HomeDashboardViewModel
    private lateinit var binding:HomeDashboardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = HomeDashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeDashboardViewModel::class.java)

        binding.fabNewRequest.setOnClickListener {
            findNavController().navigate(R.id.to_newRequestFragment)
        }
    }

}
