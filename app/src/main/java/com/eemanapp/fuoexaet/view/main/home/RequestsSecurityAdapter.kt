package com.eemanapp.fuoexaet.view.main.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelSecurityRequestBinding
import com.eemanapp.fuoexaet.databinding.ModelStaffRequestBinding
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import com.google.firebase.firestore.Query

class RequestsSecurityAdapter(private var requestClickListener: RequestClickListener)
    : RecyclerView.Adapter<RequestsSecurityAdapter.RequestsStaffViewHolder>() {

    var requests: List<Request> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsStaffViewHolder {
        val withDataBinding: ModelSecurityRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), RequestsStaffViewHolder.LAYOUT,
            parent, false)
        return RequestsStaffViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: RequestsStaffViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.request = requests[position]
            it.requestViewProfile1.setOnClickListener {
                requestClickListener.onViewProfile(requests[position])
            }
            it.requestViewProfile2.setOnClickListener {
                requestClickListener.onViewProfile(requests[position])
            }
        }
    }

    class RequestsStaffViewHolder(val viewDataBinding:
        ModelSecurityRequestBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_security_request
        }
    }
}