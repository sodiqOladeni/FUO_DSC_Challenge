package com.eemanapp.fuoexaet.view.main.home

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
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var requestId: String? = null
    private var requesterUserId: String? = null
    private var user: User? = null
    private var mAdapter: RequestsStudentAdapter? = null

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
        requestId = arguments?.getString(Constants.REQUEST_ID)
        requesterUserId = arguments?.getString(Constants.REQUESTER_USER_ID)
        user = arguments?.getParcelable(Constants.USER)
        mAdapter = RequestsStudentAdapter()

        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        requestId?.let {
            getRequestWithId()
        }

        requesterUserId?.let {
            getPreviousRequests()
        }

        binding.btnApproveRequest.setOnClickListener {
            request?.approveCoordinator =
                getString(R.string.name_template, user?.firstName, user?.lastName)
            request?.requestStatus = DiffExaetStatus.APPROVED.name
            request?.declineOrApproveTime = System.currentTimeMillis()
            Methods.showProgressBar(
                binding.progressBarApprove,
                binding.btnApproveRequest,
                listOf(binding.btnDeclineRequest)
            )
            updateRequest()
        }
        binding.btnDeclineRequest.setOnClickListener {
            request?.approveCoordinator =
                getString(R.string.name_template, user?.firstName, user?.lastName)
            request?.requestStatus = DiffExaetStatus.DECLINED.name
            request?.declineOrApproveTime = System.currentTimeMillis()
            Methods.showProgressBar(
                binding.progressBarDecline,
                binding.btnDeclineRequest,
                listOf(binding.btnApproveRequest)
            )
            updateRequest()
        }

        binding.btnOutOfSchoolRequest.setOnClickListener {
            request?.requestStatus = DiffExaetStatus.OUT_SCHOOL.name
            request?.gateDepartureTime = System.currentTimeMillis()
            updateRequest()
        }

        binding.btnCompletedRequest.setOnClickListener {
            request?.requestStatus = DiffExaetStatus.COMPLETED.name
            request?.gateArrivalTime = System.currentTimeMillis()
            updateRequest()
        }
    }

    private fun getRequestWithId() {
        viewModel.getRequestById(requestId!!).observe(this, Observer {
            binding.request = it
            request = it
            updateButtonsBasedOnRequest()
        })
    }

    private fun getPreviousRequests() {
        viewModel.getRequestForUser(requesterUserId!!)
            .observe(this, Observer { userRequests ->
                if (userRequests.isNullOrEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyData.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyData.visibility = View.GONE
                    //Update the type of count
                    binding.countAllExaet.text = userRequests.size.toString()
                    binding.countApproveExaet.text =
                        Methods.getAllRequestApprovedCount(userRequests).size.toString()
                    binding.countRejectedExaet.text =
                        Methods.getAllRequestDeclinedCount(userRequests).size.toString()
                    binding.countPendingExaet.text =
                        Methods.getAllRequestPendingCount(userRequests).size.toString()
                    //Update adapter to load previous requests
                    mAdapter?.requests = userRequests
                }
            })
    }

    private fun updateButtonsBasedOnRequest() {
        when (request?.requestStatus) {
            DiffExaetStatus.APPROVED.name -> {
                binding.approveDeclineButtonsLayout.visibility = View.GONE
                binding.outOfSchoolDeclineButtonsLayout.visibility = View.VISIBLE
                binding.approveDeclineButtonsLayout.visibility = View.GONE
                binding.outOfSchoolDeclineButtonsLayout.visibility = View.GONE
                if (Methods.userWhoCodeToName(user?.userWho!!) == Constants.SECURITY) {
                    // Only security should be able to grant passage or mark as returned
                    binding.layoutGrantPassage.visibility = View.VISIBLE
                    binding.layoutMarkAsReturn.visibility = View.VISIBLE
                    binding.btnOutOfSchoolRequest.alpha = 1F
                    binding.btnOutOfSchoolRequest.isEnabled = true
                    binding.btnOutOfSchoolRequest.isClickable = true
                    binding.btnCompletedRequest.alpha = 0.5F
                    binding.btnCompletedRequest.isEnabled = false
                    binding.btnCompletedRequest.isClickable = false
                } else {
                    // Only security should be able to grant passage or mark as returned
                    binding.layoutGrantPassage.visibility = View.VISIBLE
                    binding.layoutMarkAsReturn.visibility = View.VISIBLE
                    binding.btnCompletedRequest.alpha = 0.5F
                    binding.btnCompletedRequest.isEnabled = false
                    binding.btnCompletedRequest.isClickable = false
                    binding.btnOutOfSchoolRequest.alpha = 0.5F
                    binding.btnOutOfSchoolRequest.isEnabled = false
                    binding.btnOutOfSchoolRequest.isClickable = false
                }
            }
            DiffExaetStatus.DECLINED.name -> {
                binding.layoutRequestDeclined.visibility = View.VISIBLE
                binding.layoutRequestCompleted.visibility = View.GONE
                binding.approveDeclineButtonsLayout.visibility = View.GONE
                binding.outOfSchoolDeclineButtonsLayout.visibility = View.GONE
            }
            DiffExaetStatus.PENDING.name -> {
                binding.approveDeclineButtonsLayout.visibility = View.VISIBLE
                binding.outOfSchoolDeclineButtonsLayout.visibility = View.GONE
                binding.layoutRequestCompleted.visibility = View.GONE
                binding.layoutRequestDeclined.visibility = View.GONE

                if (Methods.userWhoCodeToName(user?.userWho!!) != Constants.COORDINATOR) {
                    binding.btnApproveRequest.alpha = 0.5F
                    binding.btnApproveRequest.isEnabled = false
                    binding.btnApproveRequest.isClickable = false
                    binding.btnDeclineRequest.alpha = 0.5F
                    binding.btnDeclineRequest.isEnabled = false
                    binding.btnDeclineRequest.isClickable = false
                }
            }
            DiffExaetStatus.OUT_SCHOOL.name -> {
                binding.approveDeclineButtonsLayout.visibility = View.GONE
                binding.outOfSchoolDeclineButtonsLayout.visibility = View.VISIBLE
                binding.layoutRequestCompleted.visibility = View.GONE
                binding.layoutRequestDeclined.visibility = View.GONE
                if (Methods.userWhoCodeToName(user?.userWho!!) == Constants.SECURITY) {
                    // Only security should be able to grant passage or mark as returned
                    binding.layoutGrantPassage.visibility = View.VISIBLE
                    binding.layoutMarkAsReturn.visibility = View.VISIBLE
                    binding.btnCompletedRequest.alpha = 1F
                    binding.btnCompletedRequest.isEnabled = true
                    binding.btnCompletedRequest.isClickable = true
                    binding.btnOutOfSchoolRequest.alpha = 0.5F
                    binding.btnOutOfSchoolRequest.isEnabled = false
                    binding.btnOutOfSchoolRequest.isClickable = false
                } else {
                    // Only security should be able to grant passage or mark as returned
                    binding.layoutGrantPassage.visibility = View.VISIBLE
                    binding.layoutMarkAsReturn.visibility = View.VISIBLE
                    binding.btnCompletedRequest.alpha = 0.5F
                    binding.btnCompletedRequest.isEnabled = false
                    binding.btnCompletedRequest.isClickable = false
                    binding.btnOutOfSchoolRequest.alpha = 0.5F
                    binding.btnOutOfSchoolRequest.isEnabled = false
                    binding.btnOutOfSchoolRequest.isClickable = false
                }

            }
            DiffExaetStatus.COMPLETED.name -> {
                binding.layoutRequestCompleted.visibility = View.VISIBLE
                binding.layoutRequestDeclined.visibility = View.GONE
                binding.approveDeclineButtonsLayout.visibility = View.GONE
                binding.outOfSchoolDeclineButtonsLayout.visibility = View.GONE
            }
        }
    }

    private fun updateRequest() {
        viewModel.updateRequest(request!!)
        viewModel.uiData.observe(this, Observer {
            Methods.hideProgressBar(
                binding.progressBarDecline,
                binding.btnDeclineRequest,
                listOf(binding.btnApproveRequest)
            )
            Methods.hideProgressBar(
                binding.progressBarApprove,
                binding.btnApproveRequest,
                listOf(binding.btnDeclineRequest)
            )

            if (it != null) {
                if (it.status!!) {
                    val dialog = Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.request_updated),
                        it.message!!
                    )
                    dialog.setCancelClickListener {
                        findNavController().popBackStack()
                    }
                } else {
                    Methods.showNotSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        it.message!!
                    )
                }
            } else {
                Methods.showNotSuccessDialog(
                    context!!,
                    getString(R.string.error_occur),
                    getString(R.string.please_check_your_internet)
                )
            }
        })
    }
}
