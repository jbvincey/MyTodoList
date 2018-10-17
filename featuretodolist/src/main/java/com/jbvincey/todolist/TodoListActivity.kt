package com.jbvincey.todolist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jbvincey.navigation.NavigationHandler
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
        const val CHECKABLE_CELL_UPDATE_DELAY = 150L
    }

    private val viewModel: TodoListArchViewModel by viewModel()

    private val navigationHandler: NavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)
        setSupportActionBar(toolbar)

        initView()
    }

    private fun initView() {
        initFabButton()
        initRecycler()
    }

    private fun initFabButton() {
        fabButton.setOnClickListener {
            startActivity(navigationHandler.buildAddTodoIntent(this))
        }
    }

    private fun initRecycler() {
        todoRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = CheckableCellAdapter()
        todoRecyclerView.adapter = adapter
        viewModel.checkableCellViewModelList.observe(this, Observer {
            updateCheckableCellListWithDelay(it, adapter)
        })
    }

    private fun updateCheckableCellListWithDelay(checkableCellList: List<CheckableCellViewModel>?,
                                                 adapter: CheckableCellAdapter) {

        Completable.timer(CHECKABLE_CELL_UPDATE_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { checkableCellList?.let(adapter::submitList) }
    }

}
