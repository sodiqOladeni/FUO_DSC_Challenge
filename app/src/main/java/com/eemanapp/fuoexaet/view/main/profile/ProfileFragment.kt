package com.eemanapp.fuoexaet.view.main.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.api.load
import coil.transform.CircleCropTransformation

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ProfileFragmentBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.DatePickerListener
import com.eemanapp.fuoexaet.model.Colleges
import com.eemanapp.fuoexaet.model.Dept
import com.eemanapp.fuoexaet.model.Hall
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.DatePickerFragment
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import javax.inject.Inject

class ProfileFragment : Fragment(), Injectable, DatePickerListener {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: ProfileFragmentBinding
    private var user: User? = null
    private val PICK_IMAGE = 12
    private var userImagePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        binding.lifecycleOwner = this
        val b = Bundle()

        viewModel.user.observe(this, Observer {
            it?.let { t ->
                user = t
                setupUser()
            }
        })

        binding.studentLayout.btnUpdateProfile.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)) {
                verifyStudentInput()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        binding.staffLayout.btnUpdateProfile.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)) {
                verifyStaffInput()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.studentLayout.editUserImage.setOnClickListener {
            pickImage()
        }

        binding.staffLayout.cardCreateCoordinator.setOnClickListener {
            b.putString(Constants.USER_WHO, Constants.COORDINATOR)
            b.putParcelable(Constants.USER, user)
            findNavController().navigate(R.id.to_signupStaffFragment, b)
        }

        binding.staffLayout.cardCreateSecurity.setOnClickListener {
            b.putString(Constants.USER_WHO, Constants.SECURITY)
            b.putParcelable(Constants.USER, user)
            findNavController().navigate(R.id.to_signupStaffFragment, b)
        }

        binding.staffLayout.changePassword.setOnClickListener {
            b.putParcelable(Constants.USER, user)
            findNavController().navigate(R.id.to_passwordResetFragment, b)
        }

        binding.studentLayout.changePassword.setOnClickListener {
            b.putParcelable(Constants.USER, user)
            findNavController().navigate(R.id.to_passwordResetFragment, b)
        }
    }

    private fun setupUser() {
        user.let {
            if (Methods.userWhoCodeToName(it?.userWho!!) == Constants.STUDENT) {
                binding.studentLayout.user = it
                binding.staffLayout.parentStaffLayout.visibility = View.GONE
                binding.studentLayout.parentStudentLayout.visibility = View.VISIBLE
            } else {
                binding.staffLayout.user = it
                binding.staffLayout.parentStaffLayout.visibility = View.VISIBLE
                binding.studentLayout.parentStudentLayout.visibility = View.GONE
                if (Methods.userWhoCodeToName(it.userWho) == Constants.COORDINATOR) {
                    binding.staffLayout.layoutAccountCreation.visibility = View.VISIBLE
                } else {
                    binding.staffLayout.layoutAccountCreation.visibility = View.GONE
                }
            }
        }
        clickListeners()
    }

    private fun verifyStudentInput() {
        val fn = binding.studentLayout.editFn
        val ln = binding.studentLayout.editLn
        val matric = binding.studentLayout.editMatric
        val em = binding.studentLayout.editEmail
        val pn = binding.studentLayout.signupPhoneNumber
        val college = binding.studentLayout.editCollege
        val dept = binding.studentLayout.editDept
        val yearEntry = binding.studentLayout.editYear
        val hall = binding.studentLayout.editHall
        val hallNumber = binding.studentLayout.editHallRoomNumber

        fn.error = null
        ln.error = null
        matric.error = null
        em.error = null
        pn.error = null
        college.error = null
        dept.error = null
        yearEntry.error = null
        hall.error = null
        hallNumber.error = null

        var isValid = true
        var focusView: View? = null

        if (fn.text.toString().isNullOrEmpty()) {
            fn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = fn
        }

        if (ln.text.toString().isNullOrEmpty()) {
            ln.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = ln
        }

        if (matric.text.toString().isNullOrEmpty()) {
            matric.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = matric
        }

        if (!Methods.isValidEmail(em.text.toString())) {
            em.error = getString(R.string.invalid_email)
            isValid = false
            focusView = em
        }

        if (pn.text.toString().isNullOrEmpty()) {
            pn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = pn
        }

        if (college.text.toString().isNullOrEmpty()) {
            college.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = college
        }

        if (dept.text.toString().isNullOrEmpty()) {
            dept.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = dept
        }

        if (yearEntry.text.toString().isNullOrEmpty()) {
            yearEntry.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = yearEntry
        }

        if (hall.text.toString().isNullOrEmpty()) {
            hall.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = hall
        }

        if (hallNumber.text.toString().isNullOrEmpty()) {
            hallNumber.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = hallNumber
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)
            Methods.showProgressBar(
                binding.studentLayout.progressBar, binding.studentLayout.btnUpdateProfile,
                listOf(binding.studentLayout.editUserImage)
            )

            user?.firstName = fn.text.toString()
            user?.lastName = ln.text.toString()
            user?.schoolId = matric.text.toString()
            user?.phoneNumber = pn.text.toString()
            user?.college = college.text.toString()
            user?.dept = dept.text.toString()
            user?.entryYear = yearEntry.text.toString()
            user?.hallOfResidence = hall.text.toString()
            user?.hallRoomNumber = hall.text.toString()
            updateUser(user!!)
        } else {
            focusView?.requestFocus()
        }
    }

    private fun verifyStaffInput() {
        val fn = binding.staffLayout.editFn
        val ln = binding.staffLayout.editLn
        val em = binding.staffLayout.editEmail
        val id = binding.staffLayout.editMatric
        val pn = binding.staffLayout.signupPhoneNumber

        fn.error = null
        ln.error = null
        em.error = null
        id.error = null
        pn.error = null

        var isValid = true
        var focusView: View? = null

        if (fn.text.toString().isNullOrEmpty()) {
            fn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = fn
        }

        if (ln.text.toString().isNullOrEmpty()) {
            ln.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = ln
        }

        if (!Methods.isValidEmail(em.text.toString())) {
            em.error = getString(R.string.invalid_email)
            isValid = false
            focusView = em
        }

        if (id.text.toString().isNullOrEmpty()) {
            id.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = id
        }

        if (pn.text.toString().isNullOrEmpty()) {
            pn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = pn
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)
            Methods.showProgressBar(
                binding.staffLayout.progressBar, binding.staffLayout.btnUpdateProfile, listOf()
            )

            user?.firstName = fn.text.toString()
            user?.lastName = ln.text.toString()
            user?.schoolId = id.text.toString()
            user?.email = em.text.toString()
            user?.phoneNumber = pn.text.toString()
            updateUser(user!!)
        } else {
            focusView?.requestFocus()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // Do other work with full size photo saved in locationForPhotos
            userImagePath = data?.data
            if (userImagePath != null) {
                if (Methods.userWhoCodeToName(user?.userWho!!) == Constants.STUDENT) {
                    binding.studentLayout.profileImage.load(userImagePath) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.image_selection_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateUser(user: User) {
        viewModel.updateUserData(user)
        viewModel.uiData.observe(this, Observer {

            if (Methods.userWhoCodeToName(user.userWho) == getString(R.string.student)) {
                Methods.hideProgressBar(
                    binding.studentLayout.progressBar, binding.studentLayout.btnUpdateProfile,
                    listOf(binding.studentLayout.editUserImage)
                )
            } else {
                Methods.hideProgressBar(
                    binding.staffLayout.progressBar, binding.staffLayout.btnUpdateProfile,
                    listOf()
                )
            }

            if (it != null) {
                if (it.status!!) {
                    Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.profile_updated),
                        it.message!!
                    )
                } else {
                    Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        it.message!!
                    )
                }
            } else {
                Methods.showSuccessDialog(
                    context!!,
                    getString(R.string.error_occur),
                    getString(R.string.please_check_your_internet)
                )
            }
        })

    }

    private fun clickListeners() {
        var ds = arrayListOf<Dept>()

        val c = SimpleSearchDialogCompat(context, "Select College",
            "Search for your college", null, Colleges.fuoColleges(),
            SearchResultListener<Colleges> { dialog, item, _ ->
                binding.studentLayout.editCollege.setText(item.title)
                ds = Colleges.deptByCollege(item.position)
                dialog.dismiss()
            })

        val h = SimpleSearchDialogCompat(context, "Select Hostel",
            "Search for your hostel", null, Colleges.hallInFUO(),
            SearchResultListener<Hall> { dialog, item, _ ->
                binding.studentLayout.editHall.setText(item.title)
                dialog.dismiss()
            })

        binding.studentLayout.editCollege.setOnClickListener {
            c.show()
        }

        val date = DatePickerFragment()
        date.listener = this

        binding.studentLayout.editDept.setOnClickListener {
            //Setup dept dialog
            SimpleSearchDialogCompat(context, "Select Department",
                "Search for your department", null, ds,
                SearchResultListener<Dept> { dialog, item, _ ->
                    binding.studentLayout.editDept.setText(item.title)
                    dialog.dismiss()
                }).show()
        }

        binding.studentLayout.editYear.setOnClickListener {
            date.show(fragmentManager!!, "datePicker")
        }

        binding.studentLayout.editHall.setOnClickListener {
            h.show()
        }
    }

    override fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        binding.studentLayout.editYear.setText(year.toString())
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }
}
