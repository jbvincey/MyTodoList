package com.jbvincey.todolist

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.ui.appbar.AppbarElevationRecyclerScrollListener
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellAdapter
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellView
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellViewModel
import com.jbvincey.ui.recycler.cells.helpers.SwipeController
import com.jbvincey.ui.recycler.cells.helpers.SwipeControllerListener
import com.jbvincey.ui.recycler.cells.helpers.SwipeControllerModel
import com.jbvincey.ui.utils.activity.displayActionSnack
import com.jbvincey.ui.utils.activity.displaySnack
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.activity_todo_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TodoListActivity : AppCompatActivity() {

    companion object {
        private const val CHECKABLE_CELL_UPDATE_DELAY = 300L
        private const val RECYCLER_TRANSITION_ANIMATION_DURATION = 150L
    }

    private lateinit var recyclerTransitionAnimation: Transition

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
        observeDeleteTodoState()
        observeArchiveTodoState()
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

        val swipeControllerModelStart = SwipeControllerModel(
                getColor(R.color.colorAccent),
                getDrawable(R.drawable.ic_baseline_delete_white_24px),
                resources.getDimensionPixelSize(R.dimen.swipe_aciton_margin),
                SwipeControllerListener { view -> onViewSwipedStart(view) }
        )
        val swipeControllerModelEnd = SwipeControllerModel(
                getColor(R.color.colorAccent),
                getDrawable(R.drawable.ic_baseline_archive_white_24px),
                resources.getDimensionPixelSize(R.dimen.swipe_aciton_margin),
                SwipeControllerListener { view -> onViewSwipedEnd(view) }
        )

        val itemTouchHelper = ItemTouchHelper(SwipeController(swipeControllerModelStart, swipeControllerModelEnd, this))
        itemTouchHelper.attachToRecyclerView(todoRecyclerView)

        recyclerTransitionAnimation = ChangeBounds()
        recyclerTransitionAnimation.duration = RECYCLER_TRANSITION_ANIMATION_DURATION

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
                    TransitionManager.beginDelayedTransition(todoRecyclerView, recyclerTransitionAnimation)
                    checkableCellList?.let(adapter::submitList)
                }
    }

    private fun onViewSwipedStart(view: View) {
        viewModel.deleteTodo((view as CheckableCellView).getViewModelId())
    }

    private fun observeDeleteTodoState() {
        viewModel.deleteTodoState.observe(this, Observer { state ->
            when (state) {
                is DeleteTodoState.Success -> displaySnack(R.string.delete_success)
                is DeleteTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { /*TODO*/ }
            }
        })
    }

    private fun onViewSwipedEnd(view: View) {
        viewModel.archiveTodo((view as CheckableCellView).getViewModelId())
    }

    private fun observeArchiveTodoState() {
        viewModel.archiveTodoState.observe(this, Observer { state ->
            when (state) {
                is ArchiveTodoState.Success -> displaySnack(R.string.archive_success)
                is ArchiveTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { /*TODO*/ }
            }
        })
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