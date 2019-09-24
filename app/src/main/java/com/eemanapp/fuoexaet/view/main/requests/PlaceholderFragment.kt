package com.eemanapp.fuoexaet.view.main.requests

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentRequestsContentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.view.main.home.RequestsStaffAdapter
import com.eemanapp.fuoexaet.view.main.home.RequestsStudentAdapter
import com.eemanapp.fuoexaet.viewModel.RequestsViewModel
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment(), Injectable, RequestClickListener {

    private lateinit var viewModel: RequestsViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var mContext: Context? = null
    private var isUpdateInProgress = false
    private lateinit var binding: FragmentRequestsContentBinding
    private var user: User? = null
    private var sectionNumber: Int? = null
    private var requestsStudentAdapter: RequestsStudentAdapter? = null
    private var requestsStaffAdapter: RequestsStaffAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = getContext()
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentRequestsContentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(RequestsViewModel::class.java).apply {
                sectionNumber = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
                setIndex(sectionNumber!!)
            }
        requestsStudentAdapter = RequestsStudentAdapter()
        requestsStaffAdapter = RequestsStaffAdapter(this)
        getUser()
    }

    private fun getUser() {
        viewModel.user.observe(this, Observer { dbuser ->
            dbuser?.let {
                user = it
                setupUI()
            }
        })
    }

    private fun setupUI() {
        Log.v("PlaceHolderFragment", "FromUser ==> ${user.toString()}")
        if (Methods.userWhoCodeToName(user!!.userWho) == Constants.STUDENT) {
            binding.recyclerView.apply {
                adapter = requestsStudentAdapter
                layoutManager = LinearLayoutManager(context)
            }
            viewModel.requests.observe(this, Observer {
                Log.v("PlaceHolderFragment", "FromDb ==> $it")
                binding.progressBar.visibility = View.GONE
                if (it.isNullOrEmpty()) {

                } else {
                    requestsStudentAdapter?.requests = it
                }
            })
        } else {
            binding.recyclerView.apply {
                adapter = requestsStaffAdapter
                layoutManager = LinearLayoutManager(context)
            }

            viewModel.requests.observe(this, Observer {
                binding.progressBar.visibility = View.GONE
                if (it.isNullOrEmpty()) {

                } else {
                    when (sectionNumber) {
                        1 -> {
                            requestsStaffAdapter?.requests = it
                        }
                        2 -> {
                            requestsStaffAdapter?.requests = Methods.getAllRequestPendingCount(it)
                        }
                        3 -> {
                            requestsStaffAdapter?.requests = Methods.getAllRequestApprovedCount(it)
                        }
                        4 -> {
                            requestsStaffAdapter?.requests = Methods.getAllRequestDeclinedCount(it)
                        }
                    }
                }
            })
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
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