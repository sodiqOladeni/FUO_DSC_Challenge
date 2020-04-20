package com.eemanapp.fuoexaet.view.main.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentResetPasswordBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.ChangePasswordViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class PasswordResetFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentResetPasswordBinding
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ChangePasswordViewModel::class.java)
        user = arguments?.getParcelable(Constants.USER)

        binding.btnChangePassword.setOnClickListener {
            if (Methods.isNetworkAvailable(requireContext())) {
                verifyInput()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun verifyInput() {
        val oldPassword = binding.oldPassword
        val newPassword1 = binding.newPassword
        val newPassword2 = binding.confirmNewPassword

        oldPassword.error = null
        newPassword1.error = null
        newPassword2.error = null

        var isVlaid = true
        var focusView: View? = null

        if (oldPassword.text.toString().isNullOrEmpty()) {
            oldPassword.error = getString(R.string.field_cant_be_empty)
            isVlaid = false
            focusView = oldPassword
        }

        if (newPassword1.text.toString().isNullOrEmpty()) {
            newPassword1.error = getString(R.string.field_cant_be_empty)
            isVlaid = false
            focusView = newPassword1
        }

        if (!Methods.isValidPassword(newPassword1.text.toString())) {
            newPassword1.error = getString(R.string.invalid_password)
            isVlaid = false
            focusView = newPassword1
        }

        if (newPassword2.text.toString().isNullOrEmpty()) {
            newPassword2.error = getString(R.string.field_cant_be_empty)
            isVlaid = false
            focusView = newPassword2
        }

        if (!Methods.isValidPassword(newPassword2.text.toString())) {
            newPassword2.error = getString(R.string.invalid_password)
            isVlaid = false
            focusView = newPassword2
        }

        if (newPassword2.text.toString().isNullOrEmpty()
            != newPassword2.text.toString().isNullOrEmpty()
        ) {
            newPassword2.error = getString(R.string.password_dont_match)
            isVlaid = false
            focusView = newPassword2
        }

        if (isVlaid) {
            Methods.hideSoftKey(requireActivity())
            Methods.showProgressBar(
                binding.progressBar, binding.btnChangePassword,
                listOf()
            )
            changePassword(
                user?.email!!,
                oldPassword.text.toString(), newPassword1.text.toString()
            )
        } else {
            focusView?.requestFocus()
        }
    }

    private fun changePassword(email: String, oldPassword: String, newPassword: String) {
        viewModel.resetPasswordWithEmail(email, oldPassword, newPassword)
            .observe(viewLifecycleOwner, Observer {
                Methods.hideProgressBar(
                    binding.progressBar, binding.btnChangePassword,
                    listOf()
                )

                it?.let { uiData ->
                    if (uiData.status!!) {
                        Methods.showSuccessDialog(
                            requireContext(),
                            getString(R.string.password_changed),
                            uiData.message!!
                        ).setOnDismissListener {
                            findNavController().navigateUp()
                        }
                    } else {
                        Methods.showNotSuccessDialog(
                            requireContext(),
                            getString(R.string.error_occur),
                            uiData.message!!
                        )
                    }
                }
            })
    }
}
