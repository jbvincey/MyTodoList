package com.jbvincey.ui.recycler.cells

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
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