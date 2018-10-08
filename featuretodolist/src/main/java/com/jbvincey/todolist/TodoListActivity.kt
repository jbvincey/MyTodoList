package com.jbvincey.todolist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jbvincey.core.navigation.NavigationHandler
import com.jbvincey.ui.recycler.cells.todo.TodoListAdapter

import kotlinx.android.synthetic.main.activity_todo_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class TodoListActivity : AppCompatActivity() {

    private val viewModel: TodoListArchViewModel by viewModel()

    private val navigationHandler: NavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)
        setSupportActionBar(toolbar)

        fabButton.setOnClickListener {
            startActivity(navigationHandler.buildAddTodoIntent(this))
        }

        todoRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = TodoListAdapter()
        todoRecyclerView.adapter = adapter
        viewModel.todoViewModelList.observe(this, Observer {
            it?.let(adapter::submitList)
        })
    }

}
