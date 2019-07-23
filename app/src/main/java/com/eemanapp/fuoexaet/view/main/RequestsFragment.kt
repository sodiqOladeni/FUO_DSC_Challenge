package com.eemanapp.fuoexaet.view.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.viewModel.RequestsViewModel

class RequestsFragment : Fragment() {

    companion object {
        fun newInstance() = RequestsFragment()
    }

    private lateinit var viewModel: RequestsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.requests_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RequestsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
