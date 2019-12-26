package com.eemanapp.fuoexaet.view.main.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.HomeDashboardFragmentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.FilterButtonListener
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Filter
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.HomeDashboardViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class HomeDashboardFragment : Fragment(), Injectable,
    RequestClickListener, FilterButtonListener, SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        val newText = query?.toLowerCase()

        val newData = requests?.filter { it.user?.firstName?.contains(newText!!, true)!!
                || it.user?.lastName?.contains(newText!!, true)!! || it.location.contains(newText!!, true)}

        when (user?.userWho){
            0 -> requestsStudentAdapter?.submitList(newData)
            1 -> requestsStaffAdapter?.submitList(newData)
            2 -> requestsSecurityAdapter?.submitList(newData)
            3 -> requestsHODAdapter?.submitList(newData)
        }
        return true
    }

    override fun onFilterButtonClicked(filter: Filter) {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        setupUser(filter)
    }

    companion object {
        fun newInstance() = HomeDashboardFragment()
    }

    private var requestsHODAdapter: RequestsHODAdapter? = null
    private var requestsStaffAdapter: RequestsStaffAdapter? = null
    private var requestsStudentAdapter: RequestsStudentAdapter? = null
    private var requestsSecurityAdapter: RequestsSecurityAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeDashboardViewModel
    private lateinit var binding: HomeDashboardFragmentBinding
    private var user: User? = null
    private val TAG = "HomeDashboardFragment"
    private var filterDialog: FilterDialog? = null
    private var isUpdateInProgress = false
    private var today: Int = 0
    private var thisMonth: Int = 0
    private var thisYear: Int = 0
    private var requests:List<Request>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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

        filterDialog = FilterDialog()
        filterDialog?.setFilterListener(this)

        requestsHODAdapter = RequestsHODAdapter(this)
        requestsStaffAdapter = RequestsStaffAdapter(this)
        requestsStudentAdapter = RequestsStudentAdapter()
        requestsSecurityAdapter = RequestsSecurityAdapter(this)

        val dateTime = Date(System.currentTimeMillis())
        today = dateTime.day
        thisMonth = dateTime.month
        thisYear = dateTime.year
        getUser()

        binding.btnFilter.setOnClickListener {
            if (filterDialog?.isAdded!!) {
                return@setOnClickListener
            }
            filterDialog?.show(childFragmentManager, "show_filter_dialog")
        }
    }

    private fun getUser() {
        viewModel.user.observe(this, Observer { dbuser ->
            dbuser?.let {
                user = it
                setupUser(null)
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun setupUser(filter: Filter?) {
        when (Methods.userWhoCodeToName(user?.userWho!!)) {
            Constants.STUDENT -> {
                binding.fabNewRequest.visibility = View.VISIBLE
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsStudentAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.getRequests(filter).observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text = it.size.toString()
                        binding.countApproveExaet.text =
                            Methods.getAllRequestApprovedCount(it).size.toString()
                        binding.countRejectedExaet.text =
                            Methods.getAllRequestDeclinedCount(it).size.toString()
                        binding.countPendingExaet.text =
                            Methods.getAllRequestPendingCount(it).size.toString()
                        //Update adapter to load previous requests
                        requests = it
                        requestsStudentAdapter?.submitList(it)
                    }
                })
            }

            Constants.COORDINATOR -> {
                //Hide ability to send request if not student by hiding the fab
                binding.fabNewRequest.visibility = View.GONE
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsStaffAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.getRequests(filter).observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text =
                            Methods.countRequestsToday(it, today, thisMonth, thisYear).toString()
                        binding.countApproveExaet.text =
                            Methods.countRequestsApprovedToday(it, today, thisMonth, thisYear)
                                .toString()
                        binding.countRejectedExaet.text =
                            Methods.countRequestsDeclinedToday(it, today, thisMonth, thisYear)
                                .toString()
                        binding.countPendingExaet.text =
                            Methods.countRequestPendingToday(it, today, thisMonth, thisYear)
                                .toString()
                        //Update adapter to load previous requests
                        requests = it
                        requestsStaffAdapter?.submitList(it)
                    }
                })
            }

            Constants.SECURITY -> {
                //Hide ability to send request if not student by hiding the fab
                binding.fabNewRequest.visibility = View.GONE
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsSecurityAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.getRequests(filter).observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text =
                            Methods.countRequestsToday(it, today, thisMonth, thisYear).toString()
                        binding.countApproveExaet.text =
                            Methods.countRequestsApprovedToday(it, today, thisMonth, thisYear)
                                .toString()
                        binding.countRejectedExaet.text =
                            Methods.countRequestsDeclinedToday(it, today, thisMonth, thisYear)
                                .toString()
                        binding.countPendingExaet.text =
                            Methods.countRequestPendingToday(it, today, thisMonth, thisYear)
                                .toString()
                        //Update adapter to load previous requests
                        requests = it
                        requestsSecurityAdapter?.submitList(it)
                    }
                })
            }

            Constants.HOD -> {
                //Hide ability to send request if not student by hiding the fab
                binding.fabNewRequest.visibility = View.GONE
                val animation =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_bottom)
                binding.recyclerView.apply {
                    adapter = requestsHODAdapter
                    layoutManager = LinearLayoutManager(context)
                    layoutAnimation = animation
                }

                viewModel.getRequests(filter).observe(this, Observer {
                    binding.progressBar.visibility = View.GONE
                    if (it.isNullOrEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                        //Update the type of count
                        binding.countAllExaet.text =
                            Methods.countRequestsToday(it, today, thisMonth, thisYear).toString()
                        binding.countApproveExaet.text =
                            Methods.countRequestsApprovedToday(it, today, thisMonth, thisYear)
                                .toString()
                        binding.countRejectedExaet.text =
                            Methods.countRequestsDeclinedToday(it, today, thisMonth, thisYear)
                                .toString()
                        binding.countPendingExaet.text =
                            Methods.countRequestPendingToday(it, today, thisMonth, thisYear)
                                .toString()
                        //Update adapter to load previous requests
                        requests = it
                        requestsHODAdapter?.submitList(it)
                    }
                })
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
        if (Methods.isNetworkAvailable(context!!)) {
            if (request.requestType == getString(R.string.vacation_exaet) && request.hasHODApproved == false) {
                Toast.makeText(
                    context,
                    getString(R.string.hod_is_yet_to_approve_vacation_exeat), Toast.LENGTH_LONG
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
                    getString(R.string.please_wait_request_in_progress), Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Snackbar.make(
                binding.root, getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestApprove(request: Request) {
        if (Methods.isNetworkAvailable(context!!)) {
            if (request.requestType == getString(R.string.vacation_exaet) && request.hasHODApproved == false) {
                Toast.makeText(
                    context,
                    getString(R.string.hod_is_yet_to_approve_vacation_exeat), Toast.LENGTH_LONG
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
                    getString(R.string.please_wait_request_in_progress), Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onVacationApproved(
        request: Request,
        checkBoxView: CompoundButton,
        isChecked: Boolean
    ) {
        if (Methods.isNetworkAvailable(context!!)) {
            if (!isUpdateInProgress) {
                request.hasHODApproved = true
                request.approveHOD =
                    getString(R.string.name_template, user?.firstName, user?.lastName)
                request.hodApproveTime = System.currentTimeMillis()
                updateRequest(request)
            } else {
                checkBoxView.isChecked = false
                Toast.makeText(
                    context,
                    getString(R.string.please_wait_request_in_progress), Toast.LENGTH_LONG
                ).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_navigation, menu)

        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_with_student_name)
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_search) {
            true
        } else {
            item.onNavDestinationSelected(findNavController())
                    || super.onOptionsItemSelected(item)
        }
    }
}
