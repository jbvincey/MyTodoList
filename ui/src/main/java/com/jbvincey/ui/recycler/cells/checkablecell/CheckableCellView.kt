package com.jbvincey.ui.recycler.cells.checkablecell

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import com.jbvincey.design.widget.helper.SwipeableView
import com.jbvincey.ui.R
import com.jbvincey.ui.databinding.ViewCheckableCellBinding
import com.jbvincey.ui.recycler.cells.AbstractCellView


/**
 * Created by jbvincey on 14/10/2018.
 */
class CheckableCellView<T> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AbstractCellView<CheckableCellViewModel<T>, ViewCheckableCellBinding>(context, attrs, defStyleAttr), SwipeableView {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun bindViewModelToView(viewModel: CheckableCellViewModel<T>) {
        super.bindViewModelToView(viewModel)
        setBackgroundResource(viewModel.backgroundColorRes)
    }

    override fun getLayout() = R.layout.view_checkable_cell

    fun getViewModelId(): Long = binding.viewModel!!.id

    override fun isSwipeableStart(): Boolean = true

    override fun isSwipeableEnd(): Boolean = binding.viewModel!!.let { it.completed && !it.archived }

}

@BindingAdapter("cellBackgroundTint")
fun <T> CheckableCellView<T>.bindBackgroundTint(@ColorRes backgroundTintRes: Int) {
    setBackgroundResource(backgroundTintRes)
}