package com.eemanapp.fuoexaet.view.prep

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.LoginFragmentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = LoginFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this

        userWho = arguments?.getString(Constants.USER_WHO)!!
        binding.loginWho.text = getString(R.string.login_as_placeholder, userWho).replace("_", " ")
        binding.loginNotAs.text = getString(R.string.not_d_placeholder, userWho).replace("_", " ")

        val b = Bundle()
        b.putString(Constants.USER_WHO, userWho)

        binding.loginBtn.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)) {
                verifyInput()
            } else {
                Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
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


        if (userWho == Constants.STUDENT) {
            binding.contDontHaveAccount.visibility = View.VISIBLE
        } else {
            binding.contDontHaveAccount.visibility = View.GONE
        }

    }

    private fun verifyInput() {
        var isValid = true
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

    private fun processLogin(e: String, p: String) {
        Log.v("LoginViewModel", "Email: $e Password: $p UserWho: $userWho")
        viewModel.authUser(e, p, userWho).observe(this, Observer {uiData->

            Methods.hideProgressBar(
                binding.progressBar, binding.loginBtn,
                listOf(
                    binding.loginWho,
                    binding.loginForgetPassword,
                    binding.loginNotAs,
                    binding.loginSignup
                )
            )

            if (uiData != null) {
                if (uiData.status!!) {
                    startMainActivity()
                } else {
                    if (uiData.message!! == Constants.USER_NOT_PAY){
                        val d = Methods.showNormalDialog(context = context!!, title = getString(R.string.continue_to_payment),
                            message = getString(R.string.you_need_payment), confirmationMessage = getString(R.string.make_payment))

                        d.setCancelable(false)
                        d.setConfirmClickListener {
                            d.dismiss()
                            val b = Bundle()
                            b.putParcelable(Constants.USER, uiData.data as? User)
                            findNavController().navigate(R.id.to_cardPaymentFragment, b)
                        }
                        d.setCancelClickListener {
                            d.dismiss()
                            FirebaseAuth.getInstance().signOut()
                        }

                    }else{
                        Methods.showNotSuccessDialog(
                            requireContext(), getString(R.string.error_occur), uiData.message!!)
                    }
                }
            }
        })
    }

    private fun startMainActivity() {
        findNavController().navigate(R.id.to_mainActivity)
        requireActivity().finish()
    }
}
