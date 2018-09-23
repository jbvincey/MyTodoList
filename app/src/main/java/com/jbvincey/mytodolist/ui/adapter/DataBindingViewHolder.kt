package com.jbvincey.mytodolist.ui.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.jbvincey.mytodolist.BR

/**
 * Created by jbvincey on 22/09/2018.
 */
class DataBindingViewHolder<T>(private val binding: ViewDataBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }

}