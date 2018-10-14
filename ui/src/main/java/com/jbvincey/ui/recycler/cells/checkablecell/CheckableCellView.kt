package com.jbvincey.ui.recycler.cells.checkablecell

import android.content.Context
import android.util.AttributeSet
import com.jbvincey.ui.R
import com.jbvincey.ui.databinding.ViewCheckableCellBinding
import android.util.TypedValue
import com.jbvincey.ui.recycler.cells.AbstractCellView


/**
 * Created by jbvincey on 14/10/2018.
 */
class CheckableCellView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AbstractCellView<CheckableCellViewModel, ViewCheckableCellBinding>(context, attrs, defStyleAttr) {

    init {
        val horizontalPadding = context.resources.getDimensionPixelSize(R.dimen.list_margin_horizontal)
        setPadding(horizontalPadding, 0, horizontalPadding, 0)

        val backgroundAttr = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, backgroundAttr, true)
        setBackgroundResource(backgroundAttr.resourceId)

        setOnClickListener {
            binding.cellCheckableCheckbox.isChecked = !binding.cellCheckableCheckbox.isChecked
        }
    }

    override fun getLayout() = R.layout.view_checkable_cell

}