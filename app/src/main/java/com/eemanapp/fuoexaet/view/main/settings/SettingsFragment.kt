package com.eemanapp.fuoexaet.view.main.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.view.prep.PrepActivity
import com.eemanapp.fuoexaet.viewModel.SettingsViewModel
import javax.inject.Inject

class SettingsFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.settings, SettingsContent()).commit()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)

        view.findViewById<TextView>(R.id.logout).setOnClickListener {

            viewModel.deleteUserFromDb()
            viewModel.isUserSignOut.observe(this, Observer {

                if (it) {
                    val i = Intent(context, PrepActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    viewModel.doneSignOut()
                }
            })
        }
    }

}
