package com.jbvincey.ui.recycler.cells

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by jbvincey on 26/09/2018.
 */
class DataBindingViewHolder<ViewModel, Binding: ViewDataBinding>(private val view: AbstractCellView<ViewModel, Binding>)
    : RecyclerView.ViewHolder(view) {

    fun bind(viewModel: ViewModel) {
        view.bindViewModelToView(viewModel)
    }

}