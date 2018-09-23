package com.jbvincey.mytodolist.ui.adapter

import android.support.v7.util.DiffUtil
import com.jbvincey.mytodolist.R
import com.jbvincey.mytodolist.models.Todo

/**
 * Created by jbvincey on 22/09/2018.
 */
class TodoListAdapter: DataBindingListAdapter<Todo>(DiffCallback()) {

    class DiffCallback: DiffUtil.ItemCallback<Todo>() {

        override fun areItemsTheSame(p0: Todo, p1: Todo) = p0.id == p1.id

        override fun areContentsTheSame(p0: Todo, p1: Todo) = p0 == p1
    }

    override fun getItemViewType(position: Int) = R.layout.item_todo

}