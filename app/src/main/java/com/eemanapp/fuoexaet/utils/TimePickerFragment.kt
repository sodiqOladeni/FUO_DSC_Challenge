package com.eemanapp.fuoexaet.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.Time
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.eemanapp.fuoexaet.interfaces.DatePickerListener
import com.eemanapp.fuoexaet.interfaces.TimePickerListener
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    lateinit var listener: TimePickerListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of DatePickerDialog and return it
        return TimePickerDialog(context!!, this, hourOfDay, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
       listener.timeSelected(hourOfDay, minute)
    }
}