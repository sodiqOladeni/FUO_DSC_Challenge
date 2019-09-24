package com.eemanapp.fuoexaet.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelStaffRequestBinding
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request
import com.google.firebase.firestore.Query

class RequestsStaffAdapter(private var requestClickListener: RequestClickListener)
    : RecyclerView.Adapter<RequestsStaffAdapter.RequestsStaffViewHolder>() {

    var requests: List<Request> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsStaffViewHolder {
        val withDataBinding: ModelStaffRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), RequestsStaffViewHolder.LAYOUT,
            parent, false)
        return RequestsStaffViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: RequestsStaffViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.request = requests[position]

            it.requestViewProfile.setOnClickListener {
                requestClickListener.onViewProfile(requests[position])
            }
            it.requestViewProfile2.setOnClickListener {
                requestClickListener.onViewProfile(requests[position])
            }
            it.requestApprove.setOnClickListener {
                requestClickListener.onRequestApprove(requests[position])
            }
            it.requestDecline.setOnClickListener {
                requestClickListener.onRequestDecline(requests[position])
            }
        }
    }

    class RequestsStaffViewHolder(val viewDataBinding:
        ModelStaffRequestBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_staff_request
        }
    }
}