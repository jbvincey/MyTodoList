package com.jbvincey.todolist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.ui.appbar.AppbarElevationRecyclerScrollListener
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellAdapter
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.activity_todo_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TodoListActivity : AppCompatActivity() {

    companion object {
        private const val CHECKABLE_CELL_UPDATE_DELAY = 300L
        private val RECYCLER_UPDATE_ANIMATION = ChangeBounds()
    }

    private val viewModel: TodoListArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        initView()
    }

    //region view setup

    private fun initView() {
        initToolbar()
        initFabButton()
        initRecycler()
        initBottomNavigation()
        observeTodoClick()
        viewModel.showUnarchivedTodos()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initFabButton() {
        fabButton.setOnClickListener {
            startActivity(navigationHandler.buildAddTodoIntent(this))
        }
    }

    private fun initRecycler() {
        todoRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        todoRecyclerView.addOnScrollListener(AppbarElevationRecyclerScrollListener(appbarLayout, todoRecyclerView))

        val adapter = CheckableCellAdapter()
        todoRecyclerView.adapter = adapter
        viewModel.checkableCellViewModelList.observe(this, Observer {
            updateCheckableCellListWithDelay(it, adapter)
        })
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

    private fun observeTodoClick() {
        viewModel.todoClicked.observe(this, Observer { todoId ->
            startEditActivity(todoId!!)
        })
    }

    private fun updateCheckableCellListWithDelay(checkableCellList: List<CheckableCellViewModel>?,
                                                 adapter: CheckableCellAdapter) {
        Completable.timer(CHECKABLE_CELL_UPDATE_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    TransitionManager.beginDelayedTransition(todoRecyclerView, RECYCLER_UPDATE_ANIMATION)
                    checkableCellList?.let(adapter::submitList)
                }
    }

    //endregion

    //region navigation

    private fun startEditActivity(todoId: Long) {
        startActivity(navigationHandler.buildEditTodoIntent(this, todoId))
    }

    override fun onBackPressed() {
        if (viewModel.isShowingAchivedTodos()) {
            bottomNavigation.selectedItemId = R.id.action_todo_list
        } else {
            super.onBackPressed()
        }
    }

}