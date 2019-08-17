package com.eemanapp.fuoexaet.view.prep

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.LoginFragmentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.view.main.MainActivity
import com.eemanapp.fuoexaet.viewModel.LoginViewModel

class LoginFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var userWho: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this

        userWho = arguments?.getString(Constants.USER_WHO)!!
        binding.loginWho.text = getString(R.string.login_as_placeholder, userWho)
        binding.loginNotAs.text = getString(R.string.not_d_placeholder, userWho)

        val b = Bundle()
        b.putString(Constants.USER_WHO, userWho)

        binding.loginBtn.setOnClickListener {
            verifyInput()
        }

        binding.loginForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.to_forgetPasswordFragment, b)
        }

        binding.loginNotAs.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.loginSignup.setOnClickListener {
            findNavController().navigate(R.id.to_signupFragment, b)
        }
    }

    private fun verifyInput() {
        var isValid: Boolean = true
        var focusView: View? = null

        binding.loginEmail.error = null
        binding.loginPassword.error = null

        val e = binding.loginEmail.text.toString()
        val p = binding.loginPassword.text.toString()

        if (!Methods.isValidEmail(e)) {
            binding.loginEmail.error = getString(R.string.invalid_email)
            isValid = false
            focusView = binding.loginEmail
        }

        if (!Methods.isValidPassword(p)) {
            binding.loginPassword.error = getString(R.string.invalid_password)
            isValid = false
            focusView = binding.loginPassword
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)
            Methods.showProgressBar(
                binding.progressBar, binding.loginBtn,
                listOf(binding.loginForgetPassword, binding.loginNotAs, binding.loginSignup)
            )
            processLogin(e, p)
        } else {
            focusView?.requestFocus()
        }
    }

    private fun processLogin(e:String, p:String) {

        Methods.hideProgressBar(
            binding.progressBar, binding.loginBtn,
            listOf(
                binding.loginWho,
                binding.loginForgetPassword,
                binding.loginNotAs,
                binding.loginSignup
            )
        )

        viewModel.authUser(e, p, userWho)
        viewModel.uiData.observe(this, Observer {
            if (it.status!!) {
                viewModel.setUiDataToNull()
                startMainActivity()
                viewModel
            } else {

            }
        })
    }

    private fun startMainActivity() {
        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }
}
