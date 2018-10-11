package com.jbvincey.ui.recycler.cells

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.jbvincey.ui.BR

/**
 * Created by jbvincey on 26/09/2018.
 */
class DataBindingViewHolder<T>(private val binding: ViewDataBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }

}