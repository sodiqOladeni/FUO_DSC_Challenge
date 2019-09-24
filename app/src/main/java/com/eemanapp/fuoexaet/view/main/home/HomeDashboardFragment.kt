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
    private lateinit var firestore: FirebaseFirestore
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
        firestore = FirebaseFirestore.getInstance()

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

                    } else {
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

                    } else {
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

                    } else {
                        requestsStaffAdapter?.requests = it
                    }
                })
            }
        }
    }

    override fun onViewProfile(request: Request) {
        val b = Bundle()
        b.putParcelable(Constants.REQUEST, request)
        b.putParcelable(Constants.USER, user)
        findNavController().navigate(R.id.to_requestProfileDetailsFragment, b)
    }

    override fun onRequestDecline(request: Request) {
        if (!isUpdateInProgress) {
            request.requestStatus = DiffExaetStatus.DECLINED.name
            request.approveCoordinator =
                getString(R.string.name_template, user?.firstName, user?.lastName)
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
