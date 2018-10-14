package com.jbvincey.ui.recycler.cells

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.jbvincey.ui.BR

/**
 * Created by jbvincey on 14/10/2018.
 */
abstract class AbstractCellView<ViewModel, Binding : ViewDataBinding>(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    protected val binding: Binding by lazy { DataBindingUtil.inflate<Binding>(LayoutInflater.from(context), getLayout(), this, true) }

    @LayoutRes abstract fun getLayout(): Int

    fun bindViewModelToView(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}

@BindingAdapter("viewModel")
fun <ViewModel> bindViewModelToView(view: AbstractCellView<ViewModel, out ViewDataBinding>, viewModel: ViewModel) {
    view.bindViewModelToView(viewModel)
}
