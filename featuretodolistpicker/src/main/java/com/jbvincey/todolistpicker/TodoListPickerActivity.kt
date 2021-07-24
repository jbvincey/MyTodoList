package com.jbvincey.todolistpicker

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.jbvincey.core.models.TodoListWithTodos
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.todolistpicker.databinding.ActivityTodoListPickerBinding
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class TodoListPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoListPickerBinding
    private val viewModel: TodoListPickerArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityTodoListPickerBinding.inflate(layoutInflater)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        setContentView(binding.root)
        super.onCreate(savedInstanceState)

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
        setSupportActionBar(binding.todoListPickerToolbar)
    }

    private fun initFabButton() {
        binding.fabButton.setOnClickListener {
            navigationHandler.goToAddTodoList(activity = this, addTodoListButton = it)
        }
    }

    private fun initRecycler() {
        val adapter = StickyNoteCardAdapter<TodoListWithTodos>()
        binding.todoListPickerRecyclerview.run {
            layoutManager = GridLayoutManager(this@TodoListPickerActivity, 2, RecyclerView.VERTICAL, false)
            this.adapter = adapter
        }
        lifecycleScope.launch {
            viewModel.stickyNoteCardViewModelListFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { adapter.submitList(it) }
        }
    }

    //endregion

    private fun observeviewActions() {
        lifecycleScope.launch {
            viewModel.viewActionFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { action ->
                    when (action) {
                        is TodoListPickerArchViewModel.ViewAction.OpenTodoList -> openTodoList(
                            action.todoListId,
                            action.todoListColorRes,
                            action.view
                        )
                        is TodoListPickerArchViewModel.ViewAction.OpenEditTodoList -> openEditTodoList(
                            action.todoListId,
                            action.todoListColorRes,
                            action.view
                        )
                    }.exhaustive
                }
        }
    }

    private fun openTodoList(todoListId: Long, @ColorRes todoListColorRes: Int, view: View) {
        navigationHandler.goToTodoList(
            activity = this,
            todoListId = todoListId,
            backgroundColorRes = todoListColorRes,
            todoListView = view
        )
    }

    private fun openEditTodoList(todoListId: Long, @ColorRes todoListColorRes: Int, view: View) {
        navigationHandler.goToEditTodoList(
            activity = this,
            todoListId = todoListId,
            backgroundColorRes = todoListColorRes,
            todoListView = view
        )

    }
}