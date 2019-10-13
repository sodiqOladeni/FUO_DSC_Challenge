package com.eemanapp.fuoexaet.view.main.home


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentNewRequestBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.DatePickerListener
import com.eemanapp.fuoexaet.interfaces.TimePickerListener
import com.eemanapp.fuoexaet.viewModel.RequestsViewModel
import javax.inject.Inject
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.*
import com.google.android.material.snackbar.Snackbar
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class NewRequestFragment : Fragment(), Injectable, DatePickerListener, TimePickerListener {

    private lateinit var binding: FragmentNewRequestBinding
    private var dateTimePicker: DatePickerFragment? = null
    private var timePickerFragment: TimePickerFragment? = null
    private lateinit var viewModel: RequestsViewModel
    private val systemType = System.currentTimeMillis()
    private var isDepartureDate = true
    private var isDepartureTime = true
    private var departureDate: String? = Methods.formatDate(systemType)
    private var departureTime: String? = Methods.formatTime(systemType)
    private var arrivalDate: String? = null
    private var arrivalTime: String? = null
    private var requestType: String? = null
    private var user: User? = null
    private lateinit var popupMenu: PopupMenu
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val TAG = NewRequestFragment::class.java.simpleName
    private var userRequestThisMonth: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dateTimePicker = DatePickerFragment()
        timePickerFragment = TimePickerFragment()
        dateTimePicker?.listener = this
        timePickerFragment?.listener = this
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RequestsViewModel::class.java)

        user = arguments?.getParcelable(Constants.USER)
        setListeners()

        viewModel.requests.observe(this, Observer {
            userRequestThisMonth = Methods.countUserRegularRequestThisMonth(it)
            Log.v(TAG, "Exeat number this month ==> $userRequestThisMonth")
        })
    }

    private fun setListeners() {
        binding.departureDate.text = departureDate
        binding.departureTime.text = departureTime

        binding.labelDepartureDate.setOnClickListener {
            isDepartureDate = true
            dateTimePicker?.show(fragmentManager!!, "Date_Picker")
        }
        binding.departureDate.setOnClickListener {
            isDepartureDate = true
            dateTimePicker?.show(fragmentManager!!, "Date_Picker")
        }

        binding.labelArrivalDate.setOnClickListener {
            isDepartureDate = false
            dateTimePicker?.show(fragmentManager!!, "Date_Picker")
        }
        binding.arrivalDate.setOnClickListener {
            isDepartureDate = false
            dateTimePicker?.show(fragmentManager!!, "Date_Picker")
        }

        binding.labelArrivalTime.setOnClickListener {
            isDepartureTime = false
            timePickerFragment?.show(fragmentManager!!, "Time_Picker")
        }
        binding.arrivalTime.setOnClickListener {
            isDepartureTime = false
            timePickerFragment?.show(fragmentManager!!, "Time_Picker")
        }

        binding.labelDepartureTime.setOnClickListener {
            isDepartureTime = true
            timePickerFragment?.show(fragmentManager!!, "Time_Picker")
        }
        binding.departureTime.setOnClickListener {
            isDepartureTime = true
            timePickerFragment?.show(fragmentManager!!, "Time_Picker")
        }

        binding.btnSubmitRequest.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)) {
                verifyExaetRequestInput()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        popupMenu = PopupMenu(context!!, binding.requestType)
        popupMenu.menuInflater.inflate(R.menu.menu_request_type, popupMenu.menu)
        binding.requestType.setOnClickListener {
            popupRequestType()
        }
    }

    private fun verifyExaetRequestInput() {

        var isValid = true
        var focusView: View? = null
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        var message = ""
        val location = binding.requestLocation.text.toString()
        val purpose = binding.requestPurpose.text.toString()

        if (requestType.isNullOrEmpty()) {
            message += "Request Type"
            isValid = false
            focusView = binding.requestType
        } else {
            if ((requestType.equals(getString(R.string.regular_exaet), true)) and (userRequestThisMonth == 2)) {
                Methods.showNotSuccessDialog(
                    context!!,
                    getString(R.string.request_error),
                    getString(R.string.regular_request_cannot_be_made)
                )
                return
            }
        }

        if (departureDate.isNullOrEmpty()) {
            message += "Departure Date"
            isValid = false
            focusView = binding.departureDate
        }

        if (departureTime.isNullOrEmpty()) {
            message += "Departure Time"
            isValid = false
            focusView = binding.departureTime
        }

        if (arrivalDate.isNullOrEmpty()) {
            message += "Arrival Date"
            isValid = false
            focusView = binding.arrivalDate
        }

        if (arrivalTime.isNullOrEmpty()) {
            message += "Arrival Time"
            isValid = false
            focusView = binding.arrivalTime
        }

        if (location.isNullOrEmpty()) {
            binding.requestLocation.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = binding.requestLocation
        }

        if (purpose.isNullOrEmpty()) {
            binding.requestPurpose.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = binding.requestPurpose
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)
            Methods.showProgressBar(
                binding.progressBar,
                binding.btnSubmitRequest,
                listOf(
                    binding.departureDate, binding.labelDepartureDate,
                    binding.departureTime, binding.labelDepartureTime,
                    binding.arrivalDate, binding.arrivalTime,
                    binding.labelArrivalDate, binding.labelArrivalTime,
                    binding.requestType, binding.requestLocation, binding.requestPurpose
                )
            )
            val request = Request(
                requestType = requestType!!,
                requestStatus = DiffExaetStatus.PENDING.name,
                departureDate = departureDate ?: "",
                departureTime = departureTime ?: "",
                arrivalDate = arrivalDate ?: "",
                arrivalTime = arrivalTime ?: "",
                location = location,
                purpose = purpose,
                requestTime = System.currentTimeMillis(),
                user = user
            )
            submitRequest(request)
        } else {
            focusView?.requestFocus()
            toast.setText("$message Must be selected")
            toast.show()
        }
    }

    private fun popupRequestType() {
        popupMenu.setOnMenuItemClickListener {
            binding.requestType.text = it.title.toString()
            requestType = it.title.toString()
            true
        }
        popupMenu.show()
    }

    private fun submitRequest(request: Request) {
        viewModel.submitRequest(request)
        viewModel.uiData.observe(this, Observer {
            Methods.hideProgressBar(
                binding.progressBar,
                binding.btnSubmitRequest,
                listOf(
                    binding.departureDate, binding.labelDepartureDate,
                    binding.departureTime, binding.labelDepartureTime,
                    binding.arrivalDate, binding.arrivalTime,
                    binding.labelArrivalDate, binding.labelArrivalTime,
                    binding.requestType, binding.requestLocation, binding.requestPurpose
                )
            )

            if (it != null) {
                if (it.status!!) {
                    val dialog = Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.request_submitted),
                        it.message!!
                    )
                    dialog.setOnDismissListener {
                        findNavController().navigateUp()
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

    override fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val date = Methods.formatDate(year, month, dayOfMonth)
        if (isDepartureDate) {
            binding.departureDate.text = date
            departureDate = date
        } else {
            binding.arrivalDate.text = Methods.formatDate(year, month, dayOfMonth)
            arrivalDate = date
        }
    }

    override fun timeSelected(hours: Int, minutes: Int) {
        val time = Methods.formatTime(hours, minutes)
        if (isDepartureTime) {
            binding.departureTime.text = time
            departureTime = time
        } else {
            binding.arrivalTime.text = Methods.formatTime(hours, minutes)
            arrivalTime = time
        }
    }
}
