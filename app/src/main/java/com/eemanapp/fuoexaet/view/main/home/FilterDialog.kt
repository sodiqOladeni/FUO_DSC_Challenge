package com.eemanapp.fuoexaet.view.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.DialogFilterBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.DatePickerListener
import com.eemanapp.fuoexaet.interfaces.FilterButtonListener
import com.eemanapp.fuoexaet.model.Filter
import com.eemanapp.fuoexaet.utils.DatePickerFragment
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.HomeDashboardViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class FilterDialog : DialogFragment(), Injectable, DatePickerListener, View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(checkBox: CompoundButton?, isChecked: Boolean) {
        when (checkBox?.id) {

            R.id.cb_regular_exeat_type, R.id.cb_vacation_exeat_type, R.id.cb_emergency_exeat_type -> {
                Log.v(TAG, "Exeat Type Cat - ${checkBox.tag}")
                if (isChecked){
                    filtersExeatType?.add(checkBox.tag.toString())
                }else{
                    filtersExeatType?.remove(checkBox.tag.toString())
                }
            }

            R.id.cb_request_status_approved, R.id.cb_request_status_pending, R.id.cb_request_status_declined,
            R.id.cb_request_status_outschool, R.id.cb_request_status_completed -> {
                Log.v(TAG, "Exeat Status Cat - ${checkBox.tag}")
                if (isChecked){
                    filtersRequestStatus?.add(checkBox.tag.toString())
                }else{
                    filtersRequestStatus?.remove(checkBox.tag.toString())
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cb_all_exeat_type -> {
                for (allCb in exeatTypeCheckboxes) {
                    allCb.isChecked = binding.cbAllExeatType.isChecked
                }
            }
            R.id.cb_all_request_status -> {
                for (allCb in requestStatusCheckboxes) {
                    allCb.isChecked = binding.cbAllRequestStatus.isChecked
                }
            }
            R.id.cb_regular_exeat_type, R.id.cb_vacation_exeat_type, R.id.cb_emergency_exeat_type -> {
                binding.cbAllExeatType.isChecked = false
            }

            R.id.cb_request_status_approved, R.id.cb_request_status_pending, R.id.cb_request_status_declined,
            R.id.cb_request_status_outschool, R.id.cb_request_status_completed -> {
                binding.cbAllRequestStatus.isChecked = false
            }
        }
    }

    override fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        if (whichDateRange == "Start") {
            startDate = Date(year - 1900, month, dayOfMonth)
            binding.editStartingDate.setText(Methods.formatDateRemove1900(year, month, dayOfMonth))
        } else if (whichDateRange == "End") {
            endDate = Date(year - 1900, month, dayOfMonth)
            binding.editEndingDate.setText(Methods.formatDateRemove1900(year, month, dayOfMonth))
        }
    }

    private val TAG = "FilterDialog"
    private lateinit var exeatTypeCheckboxes: List<CheckBox>
    private lateinit var requestStatusCheckboxes: List<CheckBox>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeDashboardViewModel
    private lateinit var binding: DialogFilterBinding
    private lateinit var filter: Filter
    private lateinit var datePickerDialog: DatePickerFragment
    private var whichDateRange = ""
    private var filterListener: FilterButtonListener? = null
    private var filtersRequestStatus: ArrayList<String>? = null
    private var filtersExeatType: ArrayList<String>? = null
    private var startDate:Date? = null
    private var endDate:Date? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(HomeDashboardViewModel::class.java)
        binding.lifecycleOwner = this

        exeatTypeCheckboxes = listOf(
            binding.cbRegularExeatType,
            binding.cbEmergencyExeatType,
            binding.cbVacationExeatType
        )

        requestStatusCheckboxes = listOf(
            binding.cbRequestStatusApproved,
            binding.cbRequestStatusPending,
            binding.cbRequestStatusDeclined,
            binding.cbRequestStatusOutschool,
            binding.cbRequestStatusCompleted
        )

        datePickerDialog = DatePickerFragment()
        datePickerDialog.listener = this
        filter = Filter()
        filtersRequestStatus = ArrayList()
        filtersExeatType = ArrayList()

        binding.btnFilter.setOnClickListener {
            filter.exeatsType = filtersExeatType
            filter.requestsStatus = filtersRequestStatus
            filter.startDate = startDate?.time
            filter.endingDate = endDate?.time
            filterListener?.onFilterButtonClicked(filter)
            this.dismiss()
        }
        binding.btnCancelDialog.setOnClickListener {
            this.dismiss()
        }
        binding.editStartingDate.setOnClickListener {
            whichDateRange = "Start"
            if(datePickerDialog.isAdded) {
                return@setOnClickListener
            }
            datePickerDialog.show(childFragmentManager, "dialog_start_time")
        }

        binding.editEndingDate.setOnClickListener {
            whichDateRange = "End"
            if(datePickerDialog.isAdded) {
                return@setOnClickListener
            }
            datePickerDialog.show(childFragmentManager, "dialog_end_time")
        }
        setCheckBoxCheckItems()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    private fun setCheckBoxCheckItems() {
        for (cbs in exeatTypeCheckboxes) {
            cbs.setOnClickListener(this)
            cbs.setOnCheckedChangeListener(this)
        }
        for (cbs in requestStatusCheckboxes) {
            cbs.setOnClickListener(this)
            cbs.setOnCheckedChangeListener(this)
        }

        binding.cbAllExeatType.setOnClickListener(this)
        binding.cbAllRequestStatus.setOnClickListener(this)
        binding.cbAllExeatType.setOnCheckedChangeListener(this)
        binding.cbAllRequestStatus.setOnCheckedChangeListener(this)
    }

    fun setFilterListener(listener: FilterButtonListener) {
        this.filterListener = listener
    }
}