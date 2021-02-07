package com.jbvincey.todolist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
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
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellAdapter
import com.jbvincey.ui.utils.activity.displayActionSnack
import kotlinx.android.synthetic.main.activity_todo_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class TodoListActivity : AppCompatActivity() {

    private val viewModel: TodoListArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()
    private val todoListNavigationHandler: TodoListNavigationHandler by inject()
    private val backgroundColorRes: Int by lazy { todoListNavigationHandler.retrieveBackgroundColorRes(intent) }
    private val todoListId: Long by lazy { todoListNavigationHandler.retrieveTodoListId(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

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
        setSupportActionBar(todoListToolbar)
        todoListToolbar.setBackgroundColor(colorInt)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = colorInt
        viewModel.todoListName.observe(this, Observer { this.title = it })
    }

    private fun initFabButton() {
        fabButton.setOnClickListener {
            startActivity(navigationHandler.buildAddTodoIntent(
                context = this,
                todoListId = todoListId,
                backgroundColorRes = backgroundColorRes
            ))
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
        itemTouchHelper.attachToRecyclerView(todoRecyclerView)

        todoRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        todoRecyclerView.itemAnimator = DefaultItemAnimator()

        val adapter = CheckableCellAdapter<Todo>()
        todoRecyclerView.adapter = adapter
        viewModel.checkableCellViewModelList.observe(this, Observer { adapter.submitList(it) })
    }

    private fun initBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->

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
        viewModel.viewActions.observe(this, Observer { action ->
            when(action) {
                TodoListArchViewModel.ViewAction.Close -> finish()
                TodoListArchViewModel.ViewAction.DisplayUnarchived -> bottomNavigation.selectedItemId = R.id.action_todo_list
                TodoListArchViewModel.ViewAction.BackPressed -> super.onBackPressed()
                is TodoListArchViewModel.ViewAction.GoToEditTodoList -> goToEditTodoList(action.todoListId)
                is TodoListArchViewModel.ViewAction.GoToEditTodo -> goToEditTodo(action.todoId)
                is TodoListArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
                    messageRes = action.messageRes,
                    actionRes = action.actionRes,
                    action = action.action
                )
            }.exhaustive
        })
    }
    //endregion

    //region navigation

    private fun goToEditTodo(todoId: Long) {
        startActivity(navigationHandler.buildEditTodoIntent(
            context = this,
            todoId = todoId,
            backgroundColorRes = backgroundColorRes
        ))
    }

    private fun goToEditTodoList(todoListId: Long) {
        startActivity(navigationHandler.buildEditTodoListIntent(
            context = this,
            todoListId = todoListId,
            backgroundColorRes = backgroundColorRes
        ))
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