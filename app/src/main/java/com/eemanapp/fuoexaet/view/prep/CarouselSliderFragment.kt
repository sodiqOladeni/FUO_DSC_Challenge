package com.eemanapp.fuoexaet.view.prep


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentSliderBinding
import com.eemanapp.fuoexaet.di.Injectable
import com.google.android.material.bottomsheet.BottomSheetDialog




/**
 * A simple [Fragment] subclass.
 */
class CarouselSliderFragment : Fragment(), Injectable, HomeScreenInitializer {

    override fun initializeHomeScreen(view:View) {
        when (view.id){
            R.id.btn_login ->{
//                showBottomSheet(whatToContinueAs = getString(R.string.login))
                findNavController().navigate(R.id.to_loginFragment)
            }

            R.id.btn_signup -> {
//                showBottomSheet(whatToContinueAs = getString(R.string.sign_up))
                findNavController().navigate(R.id.to_signupFragment)
            }
        }
    }

    private var slideViews = intArrayOf(R.layout.carousel_slide1, R.layout.carousel_slider2)
    private lateinit var binding:FragmentSliderBinding
    private var mPagerAdapter: SliderViewPager? = null
    private var mContext:Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = getContext()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_slider, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPagerAdapter = SliderViewPager(context!!, slideViews, this)
        binding.viewPager.adapter = mPagerAdapter
        binding.layoutDots.setupWithViewPager(binding.viewPager, true)
    }

    private fun showBottomSheet(whatToContinueAs:String){
        val view = layoutInflater.inflate(R.layout.dialog_continue_as, null)

        val dialog = BottomSheetDialog(mContext!!)
        dialog.setContentView(view)

        val label = view.findViewById<TextView>(R.id.continue_label)
        label.text = getString(R.string.continue_as, whatToContinueAs)

        val student = view.findViewById<TextView>(R.id.cont_as_student)
        student.setOnClickListener {
            if (whatToContinueAs == getString(R.string.login)){
                findNavController().navigate(R.id.to_loginFragment)
            }else{
                findNavController().navigate(R.id.to_signupFragment)
            }
        }

        val security = view.findViewById<TextView>(R.id.cont_as_security)
        security.setOnClickListener {

        }
        val studentCoordinator = view.findViewById<TextView>(R.id.cont_as_student_affairs)
        studentCoordinator.setOnClickListener {

        }

        dialog.show()
    }
}
