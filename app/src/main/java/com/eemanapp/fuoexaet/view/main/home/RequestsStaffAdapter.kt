package com.eemanapp.fuoexaet.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelStaffRequestBinding
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request

class RequestsStaffAdapter(private val requestClickListener: RequestClickListener) :
    ListAdapter<Request, RequestsStaffAdapter.RequestsStaffViewHolder>(RequestStaffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsStaffViewHolder {
        return RequestsStaffViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RequestsStaffViewHolder, position: Int) {
        holder.bind(getItem(position), requestClickListener)
    }

    class RequestsStaffViewHolder private constructor(private val viewDataBinding:
        ModelStaffRequestBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_staff_request

            fun from(parent: ViewGroup): RequestsStaffViewHolder {
                val withDataBinding: ModelStaffRequestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), LAYOUT,
                    parent, false
                )
                return RequestsStaffViewHolder(withDataBinding)
            }
        }

        fun bind(request: Request,
            requestClickListener: RequestClickListener) {
            viewDataBinding.also {
                it.request = request
                it.executePendingBindings()

                it.requestViewProfile.setOnClickListener {
                    requestClickListener.onViewProfile(request)
                }
                it.requestViewProfile2.setOnClickListener {
                    requestClickListener.onViewProfile(request)
                }
                it.requestApprove.setOnClickListener {
                    requestClickListener.onRequestApprove(request)
                }
                it.requestDecline.setOnClickListener {
                    requestClickListener.onRequestDecline(request)
                }
            }
        }
    }
}

class RequestStaffCallback : DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.requestUniqueId == newItem.requestUniqueId
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }

}