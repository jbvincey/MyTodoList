package com.jbvincey.featureaddtodo.edittodo

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jbvincey.design.widget.ValidationInputEditTextListener
import com.jbvincey.featureaddtodo.R
import com.jbvincey.navigation.EditTodoNavigationHandler
import kotlinx.android.synthetic.main.activity_add_todo.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by jbvincey on 27/10/2018.
 */
class EditTodoActivity: AppCompatActivity() {

    companion object {
        private const val MENU_EDIT = Menu.FIRST
        private const val MENU_DELETE = MENU_EDIT + 1
        private const val MENU_ARCHIVE = MENU_DELETE + 1
        private const val MENU_UNARCHIVE = MENU_ARCHIVE + 1
    }

    private val viewModel: EditTodoArchViewModel by viewModel()

    private val navigationHandler: EditTodoNavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        initView()
    }

    //region view setup

    private fun initView() {
        setupTodoId()
        initToolbar()
        initEditText()

        observeEditTodoState()
        observeDeleteTodoState()
        observeArchiveTodoState()
        observeUnarchiveTodoState()
    }

    private fun setupTodoId() {
        viewModel.todoId.value = navigationHandler.retrieveTodoId(intent)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24px)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initEditText() {
        viewModel.todo.observe(this, Observer { todo ->
            addTodoEditText.setText(todo?.name)
            invalidateOptionsMenu()
        })
        addTodoEditText.validationInputEditTextListener = ValidationInputEditTextListener { saveTodo() }
    }

    //endregion

    //region user actions

    private fun observeEditTodoState() {
        viewModel.editTodoState.observe(this, Observer { state ->
            when (state) {
                is EditTodoState.Success -> finish()
                is EditTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { saveTodo() }
            }
        })
    }

    private fun saveTodo() {
        viewModel.editTodo(addTodoEditText.text.toString())
    }

    private fun observeDeleteTodoState() {
        viewModel.deleteTodoState.observe(this, Observer { state ->
            when (state) {
                is DeleteTodoState.Success -> finish()
                is DeleteTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { deleteTodo() }
            }
        })
    }

    private fun deleteTodo() {
        viewModel.deleteTodo()
    }

    private fun observeArchiveTodoState() {
        viewModel.archiveTodoState.observe(this, Observer { state ->
            when (state) {
                is ArchiveTodoState.Success -> finish()
                is ArchiveTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { archiveTodo() }
            }
        })
    }

    private fun archiveTodo() {
        viewModel.archiveTodo()
    }

    private fun observeUnarchiveTodoState() {
        viewModel.unarchiveTodoState.observe(this, Observer { state ->
            when (state) {
                is UnarchiveTodoState.Success -> displaySnack(R.string.unarchive_success, viewModel.todo.value!!.name)
                is UnarchiveTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { unarchiveTodo() }
            }
        })
    }

    private fun unarchiveTodo() {
        viewModel.unarchiveTodo()
    }

    //endregion

    //region menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear()
        val todo = viewModel.todo.value
        if (todo?.completed == true) {
            if (todo.archived) {
                addMenu(menu, MENU_UNARCHIVE, R.string.action_unarchive_todo, R.drawable.ic_baseline_unarchive_24px)
            } else {
                addMenu(menu, MENU_ARCHIVE, R.string.action_archive_todo, R.drawable.ic_baseline_archive_24px)
            }
        }
        addMenu(menu, MENU_DELETE, R.string.action_delete_todo, R.drawable.ic_baseline_delete_24px)
        addMenu(menu, MENU_EDIT, R.string.action_edit_todo, R.drawable.ic_baseline_done_24px)
        return true
    }

    private fun addMenu(menu: Menu, menuId: Int, @StringRes menuTextRest: Int, @DrawableRes menuIconRes: Int) {
        menu.add(0, menuId, Menu.NONE, menuTextRest).setIcon(menuIconRes).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            MENU_EDIT -> {
                addTodoEditText.validateText()
                true
            }
            MENU_DELETE -> {
                deleteTodo()
                true
            }
            MENU_ARCHIVE -> {
                archiveTodo()
                true
            }
            MENU_UNARCHIVE -> {
                unarchiveTodo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

    //todo move to extension function
    private fun displaySnack(@StringRes messageRes: Int, vararg formatArgs: Any) {
        hideSoftKeyboard()
        val message = getString(messageRes, *formatArgs)
        Snackbar.make(findViewById(R.id.addTodoRoot), message, Snackbar.LENGTH_LONG)
                .show()
    }

    private fun displayActionSnack(@StringRes messageRes: Int,
                                   @StringRes actionRes: Int,
                                   action: () -> Unit) {
        hideSoftKeyboard()
        Snackbar.make(findViewById(R.id.addTodoRoot), messageRes, Snackbar.LENGTH_LONG)
                .setAction(actionRes) { action() }
                .show()
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun showSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }
}
