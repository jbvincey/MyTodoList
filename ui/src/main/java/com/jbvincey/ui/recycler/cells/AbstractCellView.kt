package com.jbvincey.ui.recycler.cells

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
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

    @LayoutRes
    abstract fun getLayout(): Int

    open fun bindViewModelToView(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}
