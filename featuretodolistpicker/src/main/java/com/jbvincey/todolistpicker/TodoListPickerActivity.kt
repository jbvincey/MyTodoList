package com.jbvincey.todolistpicker

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jbvincey.core.models.TodoListWithTodos
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardAdapter
import kotlinx.android.synthetic.main.activity_todo_list_picker.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class TodoListPickerActivity : AppCompatActivity() {

    private val viewModel: TodoListPickerArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list_picker)
        initView()
        observeviewActions()
    }

    //region view setup

    private fun initView() {
        initToolbar()
        initFabButton()
        initRecycler()
    }

    private fun initToolbar() {
        setSupportActionBar(todoListPickerToolbar)
    }

    private fun initFabButton() {
        fabButton.setOnClickListener {
            startActivity(navigationHandler.buildAddTodoListIntent(context = this))
        }
    }

    private fun initRecycler() {
        val adapter = StickyNoteCardAdapter<TodoListWithTodos>()
        todoListPickerRecyclerview.run {
            layoutManager = GridLayoutManager(this@TodoListPickerActivity, 2, RecyclerView.VERTICAL, false)
            this.adapter = adapter
        }
        viewModel.stickyNoteCardViewModelList.observe(this, Observer { adapter.submitList(it) })
    }

    //endregion

    private fun observeviewActions() {
        viewModel.viewActions.observe(this, Observer { action ->
            when(action) {
                is TodoListPickerArchViewModel.ViewAction.OpenTodoList -> openTodoList(action.todoListId, action.todoListColorRes)
                is TodoListPickerArchViewModel.ViewAction.OpenEditTodoList -> openEditTodoList(action.todoListId, action.todoListColorRes)
            }.exhaustive
        })
    }

    private fun openTodoList(todoListId: Long, @ColorRes todoListColorRes: Int) {
        val intent = navigationHandler.buildTodoListIntent(
            context = this,
            todoListId = todoListId,
            backgroundColorRes = todoListColorRes
        )
        startActivity(intent)
    }

    private fun openEditTodoList(todoListId: Long, @ColorRes todoListColorRes: Int) {
        val intent = navigationHandler.buildEditTodoListIntent(
            context = this,
            todoListId = todoListId,
            backgroundColorRes = todoListColorRes
        )
        startActivity(intent)
    }
}