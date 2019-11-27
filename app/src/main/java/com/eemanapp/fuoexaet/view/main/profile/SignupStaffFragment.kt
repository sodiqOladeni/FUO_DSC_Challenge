package com.eemanapp.fuoexaet.view.main.profile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.model.User
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.SignupStaffBinding
import com.eemanapp.fuoexaet.viewModel.SignupCreateStaffViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SignupStaffFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = SignupStaffFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SignupCreateStaffViewModel
    private lateinit var binding: SignupStaffBinding
    private lateinit var userWho: String
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignupStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SignupCreateStaffViewModel::class.java)
        binding.lifecycleOwner = this

        userWho = arguments?.getString(Constants.USER_WHO)!!
        user = arguments?.getParcelable(Constants.USER)

        when (userWho) {
            getString(R.string.security), getString(R.string.student_affairs) -> {
                binding.parentSignupStaff.visibility = View.VISIBLE
                binding.signupWho.text =
                    getString(R.string.creating_as_placeholder, userWho)

                binding.signupBtn.setOnClickListener {
                    if (Methods.isNetworkAvailable(context!!)) {
                        verifyStaffInput()
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.no_internet_connection),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun verifyStaffInput() {
        val fn = binding.signupFname
        val ln = binding.signupLname
        val em = binding.signupEmail
        val id = binding.signupStaffId
        val pn = binding.signupPhoneNumber
        val pass1 = binding.signupPassword1
        val pass2 = binding.signupPassword2

        fn.error = null
        ln.error = null
        em.error = null
        id.error = null
        pn.error = null
        pass1.error = null
        pass2.error = null

        var isValid = true
        var focusView: View? = null

        if (fn.text.toString().isNullOrEmpty()) {
            fn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = fn
        }

        if (ln.text.toString().isNullOrEmpty()) {
            ln.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = ln
        }

        if (!Methods.isValidEmail(em.text.toString())) {
            em.error = getString(R.string.invalid_email)
            isValid = false
            focusView = em
        }

        if (id.text.toString().isNullOrEmpty()) {
            id.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = id
        }

        if (pn.text.toString().isNullOrEmpty()) {
            pn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = pn
        }

        if (!Methods.isValidPassword(pass1.text.toString())) {
            pass1.error = getString(R.string.invalid_password)
            isValid = false
            focusView = pass1
        }

        if (!Methods.isValidPassword(pass2.text.toString())) {
            pass2.error = getString(R.string.invalid_password)
            isValid = false
            focusView = pass2
        }

        if (pass1.text.toString() != pass2.text.toString()) {
            pass2.error = getString(R.string.password_dont_match)
            isValid = false
            focusView = pass2
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)
            Methods.showProgressBar(
                binding.progressBar, binding.signupBtn,
                listOf()
            )
            processSignup(
                User(
                    hasUserPay = true,
                    firstName = fn.text.toString(),
                    lastName = ln.text.toString(),
                    schoolId = id.text.toString(),
                    email = em.text.toString(),
                    fcmToken = "fcm_token_not_yet_captured",
                    userCreatedWhen = System.currentTimeMillis(),
                    userCreatedByWho = "${user?.firstName} ${user?.lastName}",
                    phoneNumber = pn.text.toString(),
                    imageUri = "",
                    userWho = Methods.userWhoNameToCode(userWho),
                    password = pass1.text.toString()
                )
            )
        } else {
            focusView?.requestFocus()
        }
    }


    private fun processSignup(user: User) {
        viewModel.saveUserData(user)
        .observe(this, Observer {
            Methods.hideProgressBar(binding.progressBar, binding.signupBtn, listOf())
            it?.let {
                if (it.status!!) {
                    val d = Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.account_created),
                        it.message!!
                    )
                    d.setOnDismissListener {
                        findNavController().navigateUp()
                    }
                } else {
                    Methods.showNotSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        it.message!!
                    )
                }
            }
        })
    }
}
