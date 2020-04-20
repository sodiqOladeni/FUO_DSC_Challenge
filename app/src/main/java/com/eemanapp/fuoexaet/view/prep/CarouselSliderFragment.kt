package com.eemanapp.fuoexaet.view.prep


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentSliderBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog


/**
 * A simple [Fragment] subclass.
 */
class CarouselSliderFragment : Fragment(), Injectable, View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> {
                showBottomSheet(whatToContinueAs = getString(R.string.login))
            }

            R.id.btn_signup -> {
                val b = Bundle()
                b.putString(Constants.USER_WHO, getString(R.string.student))
                findNavController().navigate(R.id.to_signupFragment, b)
            }
        }
    }
    private lateinit var binding: FragmentSliderBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_slider, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener(this)
        binding.btnSignup.setOnClickListener(this)
    }

    private fun showBottomSheet(whatToContinueAs: String) {
        val bottomView = layoutInflater.inflate(R.layout.dialog_continue_as, null)
        val dialog = BottomSheetDialog(requireContext())

        val b = Bundle()
        b.putString(Constants.USER_WHO, getString(R.string.student))

        dialog.setContentView(bottomView)
        dialog.window?.attributes?.windowAnimations = R.style.BottomAnimation

        val label = bottomView.findViewById<TextView>(R.id.continue_label)
        label.text = getString(R.string.continue_as, whatToContinueAs)

        bottomView.findViewById<RadioGroup>(R.id.user_who)
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.user_student -> {
                        b.putString(Constants.USER_WHO, getString(R.string.student))
                    }
                    R.id.user_security -> {
                        b.putString(Constants.USER_WHO, getString(R.string.security))
                    }
                    R.id.user_coordinator -> {
                        b.putString(Constants.USER_WHO, getString(R.string.student_affairs))
                    }
                    R.id.user_hod -> {
                        b.putString(Constants.USER_WHO, getString(R.string.hod))
                    }
                }
            }


        val btnContinue = bottomView.findViewById<TextView>(R.id.continue_)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            if (whatToContinueAs == getString(R.string.login)) {
                findNavController().navigate(R.id.to_loginFragment, b)
            }
        }
        dialog.show()
    }
}
