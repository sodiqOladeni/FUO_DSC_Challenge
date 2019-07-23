package com.eemanapp.fuoexaet.view.prep

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.viewModel.ForgetPasswordViewModel

class ForgetPasswordFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    private lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.forget_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ForgetPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
