package com.eemanapp.fuoexaet.view.prep

import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eemanapp.fuoexaet.di.Injectable
import com.eemanapp.fuoexaet.interfaces.DatePickerListener
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.viewModel.SignupViewModel
import ir.mirrajabi.searchdialog.core.SearchResultListener
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import com.eemanapp.fuoexaet.model.Colleges
import com.eemanapp.fuoexaet.model.Dept
import com.eemanapp.fuoexaet.model.Hall
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.DatePickerFragment
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import coil.transform.CircleCropTransformation
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.SignupStudentBinding
import com.eemanapp.fuoexaet.utils.isValidMatriculationNumber
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SignupStudentFragment : Fragment(), Injectable, DatePickerListener {

    companion object {
        fun newInstance() = SignupStudentFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: SignupStudentBinding
    private lateinit var userWho: String
    private val PERMISSIONS_REQUEST_CODE = 10
    private val PICK_IMAGE = 11
    private var userImagePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignupStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignupViewModel::class.java)
        binding.lifecycleOwner = this

        userWho = arguments?.getString(Constants.USER_WHO)!!
        val b = Bundle()
        when (userWho) {
            getString(R.string.student) -> {
                binding.signupWho.text =
                    getString(R.string.signing_as_placeholder, userWho).replace("_", " ")
                binding.signupNotAs.text =
                    getString(R.string.not_d_placeholder, userWho).replace("_", " ")

                binding.signupNotAs.setOnClickListener {
                    findNavController().navigateUp()
                }

                binding.signupLogin.setOnClickListener {
                    b.putString(Constants.USER_WHO, userWho)
                    findNavController().navigate(R.id.to_loginFragment, b)
                }

                binding.signupBtn.setOnClickListener {
                    if (Methods.isNetworkAvailable(context!!)) {
                        verifyStudentInput()
                    } else {
                        Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
                    }
                }

                binding.editUserImage.setOnClickListener {
                    pickImage()
                }
            }
        }
        clickListeners()
    }

    private fun verifyStudentInput() {
        val fn = binding.editFn
        val ln = binding.editLn
        val matric = binding.editMatric
        val em = binding.editEmail
        val pn = binding.signupPhoneNumber
        val college = binding.editCollege
        val dept = binding.editDept
        val yearEntry = binding.editYear
        val hall = binding.editHall
        val roomNumber = binding.editHallRoomNumber
        val pass1 = binding.signupPassword1
        val pass2 = binding.signupPassword2

        fn.error = null
        ln.error = null
        matric.error = null
        em.error = null
        pn.error = null
        college.error = null
        dept.error = null
        yearEntry.error = null
        hall.error = null
        roomNumber.error = null
        pass1.error = null
        pass2.error = null

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

        if (!matric.text.toString().isValidMatriculationNumber()) {
            matric.error = getString(R.string.matric_number_not_valid)
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

        if (roomNumber.text.toString().isNullOrEmpty()) {
            roomNumber.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = roomNumber
        }

        if (!Methods.isValidPassword(pass1.text.toString())) {
            pass1.error = getString(R.string.invalid_password)
            isValid = false
            focusView = pass1
        }

        if (!Methods.isValidPassword(pass2.text.toString())) {
            pass2.error = getString(R.string.invalid_password)
            isValid = false
            focusView = pass2
        }

        if (pass1.text.toString() != pass2.text.toString()) {
            pass2.error = getString(R.string.password_dont_match)
            isValid = false
            focusView = pass2
        }

        if (userImagePath == null) {
            Toast.makeText(context, getString(R.string.user_image_cant_empty), Toast.LENGTH_LONG)
                .show()
            isValid = false
        }

        if (isValid) {
            Methods.hideSoftKey(activity!!)

            Methods.showProgressBar(
                binding.progressBar, binding.signupBtn,
                listOf(binding.signupNotAs, binding.signupLogin)
            )

            processSignup(
                User(
                    hasUserPay = true,
                    firstName = fn.text.toString(),
                    lastName = ln.text.toString(),
                    schoolId = matric.text.toString().toUpperCase(),
                    email = em.text.toString(),
                    userCreatedWhen = System.currentTimeMillis(),
                    userCreatedByWho = "Self",
                    fcmToken = "fcm_token_not_yet_captured",
                    phoneNumber = pn.text.toString(),
                    imageUri = userImagePath.toString(),
                    userWho = Methods.userWhoNameToCode(userWho),
                    password = pass1.text.toString(),
                    college = college.text.toString(),
                    dept = dept.text.toString(),
                    entryYear = yearEntry.text.toString(),
                    hallOfResidence = hall.text.toString(),
                    hallRoomNumber = roomNumber.text.toString()
                )
            )
        } else {
            focusView?.requestFocus()
        }
    }

    private fun processSignup(user: User) {
        viewModel.authUserAndProceedSaving(user)
        viewModel.uiData.observe(this, Observer {

            it?.let {
                Methods.hideProgressBar(
                    binding.progressBar, binding.signupBtn,
                    listOf(binding.signupNotAs, binding.signupLogin)
                )

                if (it.status!!) {
                    val d = Methods.showSuccessDialog(
                        context!!,
                        getString(R.string.user_created),
                        it.message!!)

                    d.setConfirmClickListener {
                        findNavController().navigate(R.id.to_loginFragment)
                    }
                    viewModel.saveUiDataToDefault()
                } else {
                    Methods.showNotSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        it.message!!
                    )
                    viewModel.saveUiDataToDefault()
                }
            }
        })
    }

    private fun clickListeners() {
        var ds = arrayListOf<Dept>()

        val c = SimpleSearchDialogCompat(context, "Select College",
            "Search for your college", null, Colleges.fuoColleges(),
            SearchResultListener<Colleges> { dialog, item, _ ->
                binding.editCollege.setText(item.title)
                ds = Colleges.deptByCollege(item.position)
                dialog.dismiss()
            })

        val h = SimpleSearchDialogCompat(context, "Select Hostel",
            "Search for your hostel", null, Colleges.hallInFUO(),
            SearchResultListener<Hall> { dialog, item, _ ->
                binding.editHall.setText(item.title)
                dialog.dismiss()
            })

        binding.editCollege.setOnClickListener {
            c.show()
        }

        val date = DatePickerFragment()
        date.listener = this

        binding.editDept.setOnClickListener {
            //Setup dept dialog
            SimpleSearchDialogCompat(context, "Select Department",
                "Search for your department", null, ds,
                SearchResultListener<Dept> { dialog, item, _ ->
                    binding.editDept.setText(item.title)
                    dialog.dismiss()
                }).show()
        }

        binding.editYear.setOnClickListener {
            date.show(fragmentManager!!, "datePicker")
        }

        binding.editHall.setOnClickListener {
            h.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Take the user to the success fragment when permission is granted
                pickImage()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            // Do other work with full size photo saved in locationForPhotos
            userImagePath = data?.data
            if (userImagePath != null) {
                if (userWho == Constants.STUDENT) {
                    binding.profileImage.load(userImagePath) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                } else {
                    binding.profileImage.load(userImagePath) {
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

    override fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        binding.editYear.setText(year.toString())
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }
}
