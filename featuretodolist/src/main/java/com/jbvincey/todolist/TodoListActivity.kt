package com.jbvincey.todolist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jbvincey.core.models.Todo
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.design.widget.helper.SwipeCallback
import com.jbvincey.design.widget.helper.SwipeCallbackModel
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.navigation.TodoListNavigationHandler
import com.jbvincey.todolist.databinding.ActivityTodoListBinding
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellAdapter
import com.jbvincey.ui.utils.activity.displayActionSnack
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class TodoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoListBinding
    private val viewModel: TodoListArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()
    private val todoListNavigationHandler: TodoListNavigationHandler by inject()
    private val backgroundColorRes: Int by lazy { todoListNavigationHandler.retrieveBackgroundColorRes(intent) }
    private val todoListId: Long by lazy { todoListNavigationHandler.retrieveTodoListId(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTodoListBinding.inflate(layoutInflater)

        navigationHandler.setupEnterTransition(
            activity = this,
            rootView = binding.root
        )

        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        initView()
    }

    //region view setup

    private fun initView() {
        viewModel.setBackgroundColor(backgroundColorRes)
        viewModel.setTodoListId(todoListId)
        initToolbar()
        initFabButton()
        initRecycler()
        initBottomNavigation()

        observeViewActions()
        viewModel.showUnarchivedTodos()
    }

    private fun initToolbar() {
        val colorInt = ContextCompat.getColor(this@TodoListActivity, backgroundColorRes)
        setSupportActionBar(binding.todoListToolbar)
        binding.todoListToolbar.setBackgroundColor(colorInt)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = colorInt

        lifecycleScope.launch {
            viewModel.todoListFlowName
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { name -> this@TodoListActivity.title = name }
        }
    }

    private fun initFabButton() {
        binding.fabButton.setOnClickListener {
            navigationHandler.goToAddTodo(
                activity = this,
                todoListId = todoListId,
                backgroundColorRes = backgroundColorRes,
                addTodoButton = it
            )
        }
    }

    private fun initRecycler() {
        val swipeCallbackModelStart = SwipeCallbackModel(
            getColor(R.color.theme_background_2),
            AppCompatResources.getDrawable(this, R.drawable.ic_baseline_delete_24px),
            resources.getDimensionPixelSize(R.dimen.swipe_action_margin),
            viewModel.buildSwipeStartCallback()
        )
        val swipeCallbackModelEnd = SwipeCallbackModel(
            getColor(R.color.theme_background_2),
            AppCompatResources.getDrawable(this, R.drawable.ic_baseline_archive_24px),
            resources.getDimensionPixelSize(R.dimen.swipe_action_margin),
            viewModel.buildSwipeEndCallback()
        )

        val itemTouchHelper = ItemTouchHelper(SwipeCallback(swipeCallbackModelStart, swipeCallbackModelEnd, this))
        itemTouchHelper.attachToRecyclerView(binding.todoRecyclerView)

        binding.todoRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.todoRecyclerView.itemAnimator = DefaultItemAnimator()

        val adapter = CheckableCellAdapter<Todo>()
        binding.todoRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.checkableCellViewModelListFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { adapter.submitList(it) }
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.action_todo_list -> {
                    viewModel.showUnarchivedTodos()
                    true
                }

                R.id.action_archived_list -> {
                    viewModel.showArchivedTodos()
                    true
                }

                else -> false
            }
        }
    }

    //endregion

    //action user actions

    private fun observeViewActions() {
        lifecycleScope.launch {
            viewModel.viewActionFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { action ->
                    when (action) {
                        TodoListArchViewModel.ViewAction.Close -> finish()
                        TodoListArchViewModel.ViewAction.DisplayUnarchived -> binding.bottomNavigation.selectedItemId = R.id.action_todo_list
                        TodoListArchViewModel.ViewAction.BackPressed -> super.onBackPressed()
                        is TodoListArchViewModel.ViewAction.GoToEditTodoList -> goToEditTodoList(
                            action.todoListId)
                        is TodoListArchViewModel.ViewAction.GoToEditTodo -> goToEditTodo(action.todoId,
                            action.todoView)
                        is TodoListArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
                            messageRes = action.messageRes,
                            actionRes = action.actionRes,
                            formatArgs = *action.formatArgs,
                            action = action.action
                        )
                    }.exhaustive
                }
        }
    }
    //endregion

    //region navigation

    private fun goToEditTodo(todoId: Long, todoView: View) {
        navigationHandler.goToEditTodo(
            activity = this,
            todoId = todoId,
            backgroundColorRes = backgroundColorRes,
            todoView = todoView
        )
    }

    private fun goToEditTodoList(todoListId: Long) {
        navigationHandler.goToEditTodoList(
            activity = this,
            todoListId = todoListId,
            backgroundColorRes = backgroundColorRes
        )
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    //region menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        viewModel.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return if (viewModel.onOptionItemsSelected(item.itemId)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    //enregion

}