package com.eemanapp.fuoexaet.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelSecurityRequestBinding
import com.eemanapp.fuoexaet.interfaces.RequestClickListener
import com.eemanapp.fuoexaet.model.Request

class RequestsSecurityAdapter(private var requestClickListener: RequestClickListener) :
    ListAdapter<Request, RequestsSecurityAdapter.RequestsSecurityViewHolder>(RequestSecurityCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsSecurityViewHolder {
        return RequestsSecurityViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RequestsSecurityViewHolder, position: Int) {
        holder.bind(getItem(position), requestClickListener)
    }

    class RequestsSecurityViewHolder(
        private val viewDataBinding:
        ModelSecurityRequestBinding
    ) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_security_request

            fun from(parent: ViewGroup): RequestsSecurityViewHolder {
                val withDataBinding: ModelSecurityRequestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), LAYOUT,
                    parent, false
                )
                return RequestsSecurityViewHolder(withDataBinding)
            }
        }

         fun bind(
             request: Request,
             requestClickListener: RequestClickListener
         ) {
            viewDataBinding.also {
                it.request = request
                it.executePendingBindings()

                it.requestViewProfile1.setOnClickListener {
                    requestClickListener.onViewProfile(request)
                }
                it.requestViewProfile2.setOnClickListener {
                    requestClickListener.onViewProfile(request)
                }
            }
        }
    }

    class RequestSecurityCallback : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.requestUniqueId == newItem.requestUniqueId
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }

    }
}