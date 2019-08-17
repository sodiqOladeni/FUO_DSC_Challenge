package com.eemanapp.fuoexaet.view.prep

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

import com.eemanapp.fuoexaet.databinding.SignupFragmentBinding
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
import coil.api.load
import coil.transform.CircleCropTransformation
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.view.main.MainActivity

class SignupFragment : Fragment(), Injectable, DatePickerListener {

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var binding: SignupFragmentBinding
    private lateinit var viewModel: SignupViewModel
    private lateinit var userWho: String
    private val PERMISSIONS_REQUEST_CODE = 10
    private val PICK_IMAGE = 11
    private val PERMISSIONS_REQUIRED = arrayOf(
        android.Manifest.permission.CAMERA
    )
    private var userImagePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)
        binding.lifecycleOwner = this

        userWho = arguments?.getString(Constants.USER_WHO)!!
        val b = Bundle()
        when (userWho) {
            getString(R.string.student) -> {
                binding.studentLayout.parentSignupStudent.visibility = View.VISIBLE
                binding.staffLayout.parentSignupStaff.visibility = View.GONE
                binding.studentLayout.signupWho.text =
                    getString(R.string.signing_as_placeholder, userWho)
                binding.studentLayout.signupNotAs.text =
                    getString(R.string.not_d_placeholder, userWho)

                binding.studentLayout.signupNotAs.setOnClickListener {
                    findNavController().navigateUp()
                }

                binding.studentLayout.signupLogin.setOnClickListener {
                    b.putString(Constants.USER_WHO, userWho)
                    findNavController().navigate(R.id.to_loginFragment, b)
                }

                binding.studentLayout.signupBtn.setOnClickListener {
                    verifyStudentInput()
                }

                binding.studentLayout.editUserImage.setOnClickListener {
                    pickImage()
                }
            }

            getString(R.string.security), getString(R.string.student_affairs) -> {
                binding.studentLayout.parentSignupStudent.visibility = View.GONE
                binding.staffLayout.parentSignupStaff.visibility = View.VISIBLE
                binding.staffLayout.signupWho.text =
                    getString(R.string.signing_as_placeholder, userWho)
                binding.staffLayout.signupNotAs.text =
                    getString(R.string.signing_as_placeholder, userWho)

                binding.staffLayout.signupNotAs.setOnClickListener {
                    findNavController().navigateUp()
                }

                binding.staffLayout.signupLogin.setOnClickListener {
                    b.putString(Constants.USER_WHO, userWho)
                    findNavController().navigate(R.id.to_loginFragment, b)
                }

                binding.staffLayout.signupBtn.setOnClickListener {
                    verifyStaffInput()
                }

                binding.staffLayout.editUserImage.setOnClickListener {
                    pickImage()
                }
            }
        }

        clickListeners()
    }

    private fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun verifyStaffInput() {
        val fn = binding.staffLayout.signupName
        val em = binding.staffLayout.signupEmail
        val id = binding.staffLayout.signupStaffId
        val pn = binding.staffLayout.signupPhoneNumber
        val pass1 = binding.staffLayout.signupPassword1
        val pass2 = binding.staffLayout.signupPassword2

        fn.error = null
        em.error = null
        id.error = null
        pn.error = null
        pass1.error = null
        pass2.error = null

        var isValid = true
        var focusView: View? = null

        if (fn.text.toString().isNullOrEmpty()) {
            fn.error = getString(R.string.field_cant_be_empty)
            isValid = false
            focusView = fn
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
                binding.staffLayout.progressBar, binding.staffLayout.signupBtn,
                listOf(binding.staffLayout.signupNotAs, binding.staffLayout.signupLogin)
            )
            processSignup(
                User(
                    fullName = fn.text.toString(),
                    schoolId = id.text.toString(),
                    email = em.text.toString(),
                    phoneNumber = pn.text.toString(),
                    imageUri = userImagePath.toString(),
                    userWho = Methods.userWhoNameToCode(userWho),
                    password = pass1.text.toString()
                )
            )
        } else {
            focusView?.requestFocus()
        }
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
        val pass1 = binding.staffLayout.signupPassword1
        val pass2 = binding.staffLayout.signupPassword2

        fn.error = null
        ln.error = null
        matric.error = null
        em.error = null
        pn.error = null
        college.error = null
        dept.error = null
        yearEntry.error = null
        hall.error = null
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
                binding.staffLayout.progressBar, binding.staffLayout.signupBtn,
                listOf(binding.staffLayout.signupNotAs, binding.staffLayout.signupLogin)
            )
            processSignup(
                User(
                    fullName = fn.text.toString() + " " + ln.text.toString(),
                    schoolId = matric.text.toString(),
                    email = em.text.toString(),
                    phoneNumber = pn.text.toString(),
                    imageUri = userImagePath.toString(),
                    userWho = Methods.userWhoNameToCode(userWho),
                    password = pass1.text.toString(),
                    college = college.text.toString(),
                    dept = dept.text.toString(),
                    entryYear = yearEntry.text.toString(),
                    hallOfResidence = hall.text.toString()
                )
            )
        } else {
            focusView?.requestFocus()
        }
    }

    private fun processSignup(user: User) {

        Methods.hideProgressBar(
            binding.staffLayout.progressBar, binding.staffLayout.signupBtn,
            listOf(binding.staffLayout.signupNotAs, binding.staffLayout.signupLogin)
        )

        viewModel.authUserAndProceedSaving(user)
        viewModel.uiData.observe(this, Observer {
            it.let {
                if (it.status!!) {
                    val i  = Intent(context, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    viewModel.saveUiDataToDefault()
                } else {

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
                    binding.studentLayout.profileImage.load(userImagePath) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                } else {
                    binding.staffLayout.profileImage.load(userImagePath) {
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
        binding.studentLayout.editYear.setText(year.toString())
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }
}
