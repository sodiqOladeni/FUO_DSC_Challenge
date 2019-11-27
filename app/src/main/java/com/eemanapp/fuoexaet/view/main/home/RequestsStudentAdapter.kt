package com.eemanapp.fuoexaet.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.ModelStudentRequestBinding
import com.eemanapp.fuoexaet.model.Request

class RequestsStudentAdapter
    : ListAdapter<Request, RequestsStudentAdapter.RequestsStudentViewHolder>(RequestStudentCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsStudentViewHolder {
        return RequestsStudentViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: RequestsStudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RequestsStudentViewHolder(private val viewDataBinding:
        ModelStudentRequestBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.model_student_request

            fun from(parent: ViewGroup): RequestsStudentViewHolder {
                val withDataBinding: ModelStudentRequestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), LAYOUT,
                    parent, false
                )
                return RequestsStudentViewHolder(withDataBinding)
            }
        }

             fun bind(request: Request) {
                viewDataBinding.also {
                    it.request = request
                    it.executePendingBindings()
                }
            }
    }

    class RequestStudentCallback : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.requestUniqueId == newItem.requestUniqueId
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }
    }
}