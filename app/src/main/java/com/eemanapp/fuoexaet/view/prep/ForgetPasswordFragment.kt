package com.eemanapp.fuoexaet.view.prep

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ForgetPasswordFragmentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.ForgetPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ForgetPasswordFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: ForgetPasswordViewModel
    private lateinit var binding: ForgetPasswordFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ForgetPasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ForgetPasswordViewModel::class.java)

        val userWho = arguments?.getString(Constants.USER_WHO)!!
        val b = Bundle()
        b.putString(Constants.USER_WHO, userWho)

        binding.fpLogin.setOnClickListener {
            findNavController().navigate(R.id.to_loginFragment, b)
        }

        binding.fpSignup.setOnClickListener {
            findNavController().navigate(R.id.to_signupFragment, b)
        }

        binding.fpReset.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)) {
                verifyResetInput()
            } else {
                Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun verifyResetInput() {
        binding.fpEmail.error = null

        var isValid = true
        var focusView: View? = null

        if (Methods.isValidEmail(binding.fpEmail.text.toString())) {
            binding.fpEmail.error = getString(R.string.invalid_email)
            isValid = false
            focusView = binding.fpEmail
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)
            Methods.showProgressBar(
                binding.progressBar, binding.fpReset,
                listOf(binding.fpSignup, binding.fpLogin)
            )
            resetPassword(binding.fpEmail.text.toString())
        } else {
            focusView?.requestFocus()
        }
    }

    private fun resetPassword(e: String) {

        viewModel.resetPasswordWithEmail(e)
        viewModel.uiData.observe(this, Observer {

            Methods.hideProgressBar(
                binding.progressBar, binding.fpReset,
                listOf(binding.fpSignup, binding.fpLogin)
            )

            if (it.status!!) {
                val d = Methods.showSuccessDialog(
                    context!!,
                    getString(R.string.check_email),
                    it.message!!
                )
                d.setCancelClickListener {
                    findNavController().navigateUp()
                    viewModel.uiDataToNull()
                }
            } else {
                Methods.showSuccessDialog(context!!, getString(R.string.error_occur), it.message!!)
            }
        })
    }
}
