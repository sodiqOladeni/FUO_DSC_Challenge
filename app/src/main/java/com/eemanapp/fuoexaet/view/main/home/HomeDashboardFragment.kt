package com.eemanapp.fuoexaet.view.main.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.HomeDashboardFragmentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.HomeDashboardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class HomeDashboardFragment : Fragment(), Injectable, RequestClickListener {

    companion object {
        fun newInstance() = HomeDashboardFragment()
    }

    private var requestsStaffAdapter: RequestsStaffAdapter? = null
    private var requestsStudentAdapter: RequestsStudentAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeDashboardViewModel
    private lateinit var binding: HomeDashboardFragmentBinding
    private var user: User? = null
    private var isUpdateInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeDashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(HomeDashboardViewModel::class.java)
        binding.lifecycleOwner = this

        val b = Bundle()
        binding.fabNewRequest.setOnClickListener {
            b.putParcelable(Constants.USER, user)
            findNavController().navigate(R.id.to_newRequestFragment, b)
        }
        getUser()
    }

    private fun getUser() {
        viewModel.user.observe(this, Observer { dbuser ->
            dbuser?.let {
                user = it
                setupUser()
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun setupUser() {
        requestsStaffAdapter = RequestsStaffAdapter(this)
        requestsStudentAdapter = RequestsStudentAdapter()

        when (Methods.userWhoCodeToName(user?.userWho!!)) {
            Constants.STUDENT -> {
                binding.fabNewRequest.visibility = View.VISIBLE

                binding.recyclerView.apply {
                    adapter = requestsStudentAdapter
                    layoutManager = LinearLayoutManager(context)
                }

                viewModel.requests.observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text = it.size.toString()
                        binding.countApproveExaet.text = Methods.getAllRequestApprovedCount(it).size.toString()
                        binding.countRejectedExaet.text = Methods.getAllRequestDeclinedCount(it).size.toString()
                        binding.countPendingExaet.text = Methods.getAllRequestPendingCount(it).size.toString()
                        //Update adapter to load previous requests
                        requestsStudentAdapter?.requests = it
                    }
                })
            }

            Constants.COORDINATOR -> {
                //Hide ability to send request if not student by hiding the fab
                binding.fabNewRequest.visibility = View.GONE

                binding.recyclerView.apply {
                    adapter = requestsStaffAdapter
                    layoutManager = LinearLayoutManager(context)
                }

                viewModel.requests.observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text = it.size.toString()
                        binding.countApproveExaet.text = Methods.getAllRequestApprovedCount(it).size.toString()
                        binding.countRejectedExaet.text = Methods.getAllRequestDeclinedCount(it).size.toString()
                        binding.countPendingExaet.text = Methods.getAllRequestPendingCount(it).size.toString()
                        //Update adapter to load previous requests
                        requestsStaffAdapter?.requests = it
                    }
                })
            }

            Constants.SECURITY -> {
                //Hide ability to send request if not student by hiding the fab
                binding.fabNewRequest.visibility = View.GONE

                binding.recyclerView.apply {
                    adapter = requestsStaffAdapter
                    layoutManager = LinearLayoutManager(context)
                }

                viewModel.requests.observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text = it.size.toString()
                        binding.countApproveExaet.text = Methods.getAllRequestApprovedCount(it).size.toString()
                        binding.countRejectedExaet.text = Methods.getAllRequestDeclinedCount(it).size.toString()
                        binding.countPendingExaet.text = Methods.getAllRequestPendingCount(it).size.toString()
                        //Update adapter to load previous requests
                        requestsStaffAdapter?.requests = it
                    }
                })
            }
        }
    }

    override fun onViewProfile(request: Request) {
        val b = Bundle()
        b.putString(Constants.REQUEST_ID, request.requestUniqueId)
        b.putParcelable(Constants.USER, user)
        findNavController().navigate(R.id.to_requestProfileDetailsFragment, b)
    }

    override fun onRequestDecline(request: Request) {
        if (!isUpdateInProgress) {
            request.requestStatus = DiffExaetStatus.DECLINED.name
            request.approveCoordinator =
                getString(R.string.name_template, user?.firstName, user?.lastName)
            request.declineOrApproveTime = System.currentTimeMillis()
            updateRequest(request)
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_wait_request_in_progress),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestApprove(request: Request) {
        if (!isUpdateInProgress) {
            request.requestStatus = DiffExaetStatus.APPROVED.name
            request.approveCoordinator =
                getString(R.string.name_template, user?.firstName, user?.lastName)
            request.declineOrApproveTime = System.currentTimeMillis()
            updateRequest(request)
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_wait_request_in_progress),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateRequest(request: Request) {
        viewModel.updateRequest(request)
        isUpdateInProgress = true
        viewModel.uiData.observe(this, Observer {
            isUpdateInProgress = false
        })
    }
}
