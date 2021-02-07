package com.jbvincey.ui.recycler.cells

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * Created by jbvincey on 22/09/2018.
 */
abstract class DataBindingListAdapter<ViewModel, Binding: ViewDataBinding>(
        diffCallback: DiffUtil.ItemCallback<ViewModel>
) : ListAdapter<ViewModel, DataBindingViewHolder<ViewModel, Binding>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<ViewModel, Binding> {
        val view = createView(parent.context)
        return DataBindingViewHolder(view)
    }

    abstract fun createView(context: Context): AbstractCellView<ViewModel, Binding>

    override fun onBindViewHolder(holder: DataBindingViewHolder<ViewModel, Binding>, position: Int) = holder.bind(getItem(position))

}