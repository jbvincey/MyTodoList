package com.jbvincey.ui.recycler.cells.checkablecell

import android.content.Context
import android.util.AttributeSet
import com.jbvincey.ui.R
import com.jbvincey.ui.databinding.ViewCheckableCellBinding
import com.jbvincey.ui.recycler.cells.AbstractCellView


/**
 * Created by jbvincey on 14/10/2018.
 */
class CheckableCellView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AbstractCellView<CheckableCellViewModel, ViewCheckableCellBinding>(context, attrs, defStyleAttr) {

    override fun getLayout() = R.layout.view_checkable_cell

}