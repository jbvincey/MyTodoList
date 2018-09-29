package com.jbvincey.ui.recycler.cells.todo

import android.support.v7.util.DiffUtil
import com.jbvincey.ui.R
import com.jbvincey.ui.recycler.common.DataBindingListAdapter

/**
 * Created by jbvincey on 22/09/2018.
 */
class TodoListAdapter: DataBindingListAdapter<TodoViewModel>(DiffCallback()) {

    class DiffCallback: DiffUtil.ItemCallback<TodoViewModel>() {

        override fun areItemsTheSame(p0: TodoViewModel, p1: TodoViewModel) = p0.id == p1.id

        override fun areContentsTheSame(p0: TodoViewModel, p1: TodoViewModel) = p0 == p1
    }

    override fun getItemViewType(position: Int) = R.layout.item_todo

}