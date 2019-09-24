package com.eemanapp.fuoexaet.view.main.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eemanapp.fuoexaet.R

import com.eemanapp.fuoexaet.databinding.FragmentRequestProfileBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.RequestsViewModel
import javax.inject.Inject

class RequestProfileDetailsFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = RequestProfileDetailsFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentRequestProfileBinding
    private lateinit var viewModel: RequestsViewModel
    private var request: Request? = null
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RequestsViewModel::class.java)
        binding.lifecycleOwner = this
        request = arguments?.getParcelable(Constants.REQUEST)
        user = arguments?.getParcelable(Constants.USER)

        request?.let {
            binding.request = it
        }

        binding.btnApproveRequest.setOnClickListener {
            request?.approveCoordinator = getString(R.string.name_template, user?.firstName, user?.lastName)
            request?.requestStatus = DiffExaetStatus.APPROVED.name
            Methods.showProgressBar(
                binding.progressBarApprove,
                binding.btnApproveRequest,
                listOf(binding.btnDeclineRequest)
            )
            updateRequest()
        }
        binding.btnDeclineRequest.setOnClickListener {
            request?.approveCoordinator = getString(R.string.name_template, user?.firstName, user?.lastName)
            request?.requestStatus = DiffExaetStatus.DECLINED.name
            Methods.showProgressBar(
                binding.progressBarDecline,
                binding.btnDeclineRequest,
                listOf(binding.btnApproveRequest)
            )
            updateRequest()
        }
    }

    private fun updateRequest() {
        viewModel.updateRequest(request!!)
        viewModel.uiData.observe(this, Observer {
            Methods.hideProgressBar(binding.progressBarDecline, binding.btnDeclineRequest, listOf(binding.btnApproveRequest))
            Methods.hideProgressBar(binding.progressBarApprove, binding.btnApproveRequest, listOf(binding.btnDeclineRequest))

            if (it != null){
                if (it.status!!){
                    val dialog = Methods.showSuccessDialog(context!!, getString(R.string.request_updated), it.message!!)
                    dialog.setCancelClickListener {
                        findNavController().popBackStack()
                    }
                }else{
                    Methods.showNotSuccessDialog(context!!, getString(R.string.error_occur), it.message!!)
                }
            }else{
                Methods.showNotSuccessDialog(context!!,getString(R.string.error_occur), getString(R.string.please_check_your_internet))
            }
        })
    }
}
