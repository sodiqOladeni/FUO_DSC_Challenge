package com.eemanapp.fuoexaet.view.main.requests

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
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
import com.eemanapp.fuoexaet.view.main.home.RequestsHODAdapter
import com.eemanapp.fuoexaet.view.main.home.RequestsSecurityAdapter
import com.eemanapp.fuoexaet.view.main.home.RequestsStaffAdapter
import com.eemanapp.fuoexaet.view.main.home.RequestsStudentAdapter
import com.eemanapp.fuoexaet.viewModel.RequestsViewModel
import com.google.android.material.snackbar.Snackbar
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
    private var requestsHODAdapter: RequestsHODAdapter? = null
    private var requestsStudentAdapter: RequestsStudentAdapter? = null
    private var requestsStaffAdapter: RequestsStaffAdapter? = null
    private var requestsSecurityAdapter: RequestsSecurityAdapter? = null

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
        savedInstanceState: Bundle?
    ): View? {
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
        requestsHODAdapter = RequestsHODAdapter(this)
        requestsSecurityAdapter = RequestsSecurityAdapter(this)

        getUser()
    }

    private fun getUser() {
        viewModel.user.observe(viewLifecycleOwner, Observer { dbuser ->
            dbuser?.let {
                user = it
                setupUI()
            }
        })
    }

    private fun setupUI() {

        when (Methods.userWhoCodeToName(user!!.userWho)) {
            Constants.STUDENT -> {
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsStudentAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }
                viewModel.requests.observe(viewLifecycleOwner, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.emptyData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.emptyData.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        when (sectionNumber) {
                            1 -> {
                                requestsStudentAdapter?.submitList(
                                    Methods.getAllRequestCompleted(
                                        it
                                    ))
                            }
                            2 -> {
                                requestsStudentAdapter?.submitList(
                                    Methods.getAllRequestPendingCount(
                                        it
                                    )
                                )
                            }
                            3 -> {
                                requestsStudentAdapter?.submitList(
                                    Methods.getAllRequestApprovedCount(
                                        it
                                    )
                                )
                            }
                            4 -> {
                                requestsStudentAdapter?.submitList(
                                    Methods.getAllRequestDeclinedCount(
                                        it
                                    )
                                )
                            }
                            5 -> {
                                requestsStudentAdapter?.submitList(
                                    Methods.getAllRequestOnGoing(it)
                                )
                            }
                        }
                    }
                })
            }
            Constants.COORDINATOR -> {
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsStaffAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.requests.observe(viewLifecycleOwner, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.emptyData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.emptyData.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        when (sectionNumber) {
                            1 -> {
                                requestsStaffAdapter?.submitList(it)
                            }
                            2 -> {
                                requestsStaffAdapter?.submitList(
                                    Methods.getAllRequestPendingCount(it)
                                )
                            }
                            3 -> {
                                requestsStaffAdapter?.submitList(
                                    Methods.getAllRequestApprovedCount(
                                        it
                                    )
                                )
                            }
                            4 -> {
                                requestsStaffAdapter?.submitList(
                                    Methods.getAllRequestDeclinedCount(
                                        it
                                    )
                                )
                            }
                        }
                    }
                })
            }
            Constants.SECURITY -> {
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsSecurityAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.requests.observe(viewLifecycleOwner, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.emptyData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.emptyData.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        when (sectionNumber) {
                            1 -> {
                                requestsSecurityAdapter?.submitList(it)
                            }
                            2 -> {
                                requestsSecurityAdapter?.submitList(
                                    Methods.getAllRequestPendingCount(
                                        it
                                    )
                                )
                            }
                            3 -> {
                                requestsSecurityAdapter?.submitList(
                                    Methods.getAllRequestApprovedCount(
                                        it
                                    )
                                )
                            }
                            4 -> {
                                requestsSecurityAdapter?.submitList(
                                    Methods.getAllRequestDeclinedCount(
                                        it
                                    )
                                )
                            }
                        }
                    }
                })
            }

            Constants.HOD -> {
                //Hide ability to send request if not student by hiding the fab
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsHODAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.requests.observe(viewLifecycleOwner, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        when (sectionNumber) {
                            1 -> {
                                requestsHODAdapter?.submitList(it)
                            }
                            2 -> {
                                requestsHODAdapter?.submitList(
                                    Methods.getAllRequestPendingCount(
                                        it
                                    )
                                )
                            }
                            3 -> {
                                requestsHODAdapter?.submitList(
                                    Methods.getAllRequestApprovedCount(
                                        it
                                    )
                                )
                            }
                            4 -> {
                                requestsHODAdapter?.submitList(
                                    Methods.getAllRequestDeclinedCount(
                                        it
                                    )
                                )
                            }
                        }
                    }
                })
            }
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
        b.putString(Constants.REQUEST_ID, request.requestUniqueId)
        b.putString(Constants.REQUESTER_USER_ID, request.user?.uniqueId)
        b.putParcelable(Constants.USER, user)
        findNavController().navigate(R.id.to_requestProfileDetailsFragment, b)
    }

    override fun onRequestDecline(request: Request) {
        if (Methods.isNetworkAvailable(requireContext())) {
            if (request.requestType == getString(R.string.vacation_exaet) && request.hasHODApproved == false) {
                Toast.makeText(
                    context,
                    getString(R.string.hod_is_yet_to_approve_vacation_exeat),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

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
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestApprove(request: Request) {
        if (Methods.isNetworkAvailable(requireContext())) {
            if (request.requestType == getString(R.string.vacation_exaet) && request.hasHODApproved == false) {
                Toast.makeText(
                    context,
                    getString(R.string.hod_is_yet_to_approve_vacation_exeat),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

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
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onVacationApproved(request: Request, checkBoxView: CompoundButton, isChecked:Boolean) {
        if (Methods.isNetworkAvailable(requireContext())) {
            if (!isUpdateInProgress) {
                request.hasHODApproved = true
                request.approveHOD =
                    getString(R.string.name_template, user?.firstName, user?.lastName)
                request.hodApproveTime = System.currentTimeMillis()
                updateRequest(request)
            } else {
                checkBoxView.isChecked = false
                Toast.makeText(context,
                    getString(R.string.please_wait_request_in_progress), Toast.LENGTH_LONG).show()
            }
        } else {
            checkBoxView.isChecked = false
            Snackbar.make(
                binding.root,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun updateRequest(request: Request) {
        isUpdateInProgress = true
        viewModel.updateRequest(request).observe(this, Observer {
            isUpdateInProgress = false
        })
    }
}