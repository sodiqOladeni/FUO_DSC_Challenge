package com.eemanapp.fuoexaet.view.prep

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.eemanapp.fuoexaet.R

class SliderViewPager(var mContext: Context, var mLayouts: IntArray, var screenInitializer: HomeScreenInitializer)
    : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(mLayouts[position], container, false)
        val infoView = view.findViewWithTag<ConstraintLayout>("TEXT_INFO_LAYOUT")

        val singUpButton = infoView.findViewById<Button>(R.id.btn_signup)
        val loginButton = infoView.findViewById<Button>(R.id.btn_login)
        singUpButton.setOnClickListener { v -> screenInitializer.initializeHomeScreen(v) }
        loginButton.setOnClickListener { v -> screenInitializer.initializeHomeScreen(v) }

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return mLayouts.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}