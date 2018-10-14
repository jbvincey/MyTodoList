package com.jbvincey.ui.recycler.cells.checkablecell

import android.support.v7.util.DiffUtil
import com.jbvincey.ui.R
import com.jbvincey.ui.recycler.cells.DataBindingListAdapter

/**
 * Created by jbvincey on 22/09/2018.
 */
class CheckableCellAdapter: DataBindingListAdapter<CheckableCellViewModel>(DiffCallback()) {

    class DiffCallback: DiffUtil.ItemCallback<CheckableCellViewModel>() {

        override fun areItemsTheSame(p0: CheckableCellViewModel, p1: CheckableCellViewModel) = p0.id == p1.id

        override fun areContentsTheSame(p0: CheckableCellViewModel, p1: CheckableCellViewModel) = p0 == p1
    }

    override fun getItemViewType(position: Int) = R.layout.item_checkable_cell

}