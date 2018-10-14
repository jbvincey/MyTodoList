package com.jbvincey.ui.recycler.cells

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.jbvincey.ui.BR

/**
 * Created by jbvincey on 26/09/2018.
 */
class DataBindingViewHolder<ViewModel>(private val binding: ViewDataBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }

}