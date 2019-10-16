package com.eemanapp.fuoexaet.view.main.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.model.FeatureRequestsAndErrorReport
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.view.prep.PrepActivity
import com.eemanapp.fuoexaet.viewModel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.settings_fragment.*
import javax.inject.Inject

class SettingsFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var pref: SharedPref
    private lateinit var viewModel: SettingsViewModel
    private var reportProblemDialog: AlertDialog? = null
    private var reportProblemCancel: TextView? = null
    private var reportProblemEdit: TextInputEditText? = null
    private var reportProblemOk: TextView? = null
    private var reportProblemProgressBar: ProgressBar? = null
    private var appUser: User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.settings, SettingsContent()).commit()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)
        appUser = pref.getUser()

        view.findViewById<TextView>(R.id.logout).setOnClickListener {
            viewModel.deleteUserFromDb()
            viewModel.isUserSignOut.observe(this, Observer {
                if (it) {
                    val i = Intent(context, PrepActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    viewModel.doneSignOut()
                    activity?.finish()
                }
            })
        }

        view.findViewById<TextView>(R.id.submit_feature_request).setOnClickListener {
            reportCourseProblem()
        }
    }

    private fun reportCourseProblem() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_submit_feature, null)
        reportProblemDialog = AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(dialogView)
            .create()

        reportProblemCancel = dialogView.findViewById(R.id.cancel)
        reportProblemCancel?.setOnClickListener {
            reportProblemDialog?.dismiss()
        }

        reportProblemOk = dialogView.findViewById(R.id.ok)
        reportProblemOk?.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)) {
                verifyUserInput()
            } else {
                Snackbar.make(
                    dialogView,
                    getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        reportProblemEdit = dialogView.findViewById(R.id.problem_reported)
        reportProblemProgressBar = dialogView.findViewById(R.id.progressBar)
        reportProblemDialog?.show()
    }

    private fun verifyUserInput() {
        val issue = reportProblemEdit?.text.toString()
        var focusView: View? = null
        var isEmpty = false

        reportProblemEdit?.error = null

        if (issue.isNullOrEmpty()) {
            reportProblemEdit?.error = getString(R.string.field_cant_be_empty)
            focusView = reportProblemEdit
            isEmpty = true
        }

        if (isEmpty) {
            focusView?.requestFocus()
        } else {
            Methods.hideSoftKey(activity!!)
            reportInProgress()
            processUserIssueFeatureReported(
                FeatureRequestsAndErrorReport().apply {
                    featureOrError = issue
                    user = appUser
                }
            )
        }
    }

    private fun processUserIssueFeatureReported(feature: FeatureRequestsAndErrorReport) {
        viewModel.saveFeatureRequest(feature).observe(this, Observer {
            reportNotInProgress()
            it?.let {
                if (it.status!!) {
                    Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.report_saved),
                        it.message!!
                    ).setOnDismissListener {
                        reportProblemDialog?.dismiss()
                    }
                } else {
                    Methods.showNotSuccessDialog(context!!, getString(R.string.error_occur), it.message!!)
                }
            }
        })
    }

    private fun reportInProgress() {
        reportProblemProgressBar?.visibility = View.VISIBLE
        reportProblemOk?.visibility = View.GONE
        reportProblemCancel?.isEnabled = false
    }

    private fun reportNotInProgress() {
        reportProblemProgressBar?.visibility = View.GONE
        reportProblemOk?.visibility = View.VISIBLE
        reportProblemCancel?.isEnabled = true
    }
}
