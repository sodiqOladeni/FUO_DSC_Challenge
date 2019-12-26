package com.eemanapp.fuoexaet.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelHodRequestBinding
import com.eemanapp.fuoexaet.databinding.ModelStaffRequestBinding
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request

class RequestsHODAdapter(private val requestClickListener: RequestClickListener) :
    ListAdapter<Request, RequestsHODAdapter.RequestsHODViewHolder>(RequestHODCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsHODViewHolder {
        return RequestsHODViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RequestsHODViewHolder, position: Int) {
        holder.bind(getItem(position), requestClickListener)
    }

    class RequestsHODViewHolder private constructor(private val viewDataBinding:
                                                    ModelHodRequestBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_hod_request

            fun from(parent: ViewGroup): RequestsHODViewHolder {
                val withDataBinding: ModelHodRequestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), LAYOUT,
                    parent, false
                )
                return RequestsHODViewHolder(withDataBinding)
            }
        }

        fun bind(request: Request,
            requestClickListener: RequestClickListener) {
            viewDataBinding.also {
                it.request = request
                it.executePendingBindings()

                it.approveVacation.setOnCheckedChangeListener { checkbox, isChecked ->
                    requestClickListener.onVacationApproved(request, checkbox, isChecked)
                }

                it.requestViewProfile2.setOnClickListener {
                    requestClickListener.onViewProfile(request)
                }
            }
        }
    }
}

class RequestHODCallback : DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.requestUniqueId == newItem.requestUniqueId
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }

}