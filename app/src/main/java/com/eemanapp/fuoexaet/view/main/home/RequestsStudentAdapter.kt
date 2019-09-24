package com.eemanapp.fuoexaet.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelStaffRequestBinding
import com.eemanapp.fuoexaet.databinding.ModelStudentRequestBinding
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request
import com.google.firebase.firestore.Query

class RequestsStudentAdapter
    : RecyclerView.Adapter<RequestsStudentAdapter.RequestsStudentViewHolder>() {

    var requests: List<Request> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
       return requests.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsStudentViewHolder {
        val withDataBinding: ModelStudentRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), RequestsStudentViewHolder.LAYOUT,
            parent, false)
        return RequestsStudentViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: RequestsStudentViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.request = requests[position]
        }
    }

    class RequestsStudentViewHolder(val viewDataBinding:
        ModelStudentRequestBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_student_request
        }
    }
}