package com.jbvincey.featureaddtodo.edittodo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.jbvincey.design.widget.ValidationInputEditTextListener
import com.jbvincey.featureaddtodo.R
import com.jbvincey.navigation.EditTodoNavigationHandler
import com.jbvincey.ui.utils.activity.displayActionSnack
import com.jbvincey.ui.utils.activity.displayAlertDialog
import kotlinx.android.synthetic.main.activity_add_todo.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by jbvincey on 27/10/2018.
 */
class EditTodoActivity : AppCompatActivity() {

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
        observeTodo()

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
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_white_24px)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initEditText() {
        addTodoEditText.validationInputEditTextListener = ValidationInputEditTextListener { saveTodo() }
    }

    private fun observeTodo() {
        viewModel.todo.observe(this, Observer { todo ->
            title = todo?.name
            addTodoEditText.setText(todo?.name)
            addTodoEditText.isEnabled = !(todo?.archived ?: false)
            invalidateOptionsMenu()
        })
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
                is ArchiveTodoState.Success -> if (state.displaySnack) displayActionSnack(R.string.archive_success, R.string.undo, state.todoName) { unarchiveTodo(false) }
                is ArchiveTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { archiveTodo(true) }
            }
        })
    }

    private fun archiveTodo(displaySnackOnSuccess: Boolean) {
        viewModel.archiveTodo(displaySnackOnSuccess)
    }

    private fun observeUnarchiveTodoState() {
        viewModel.unarchiveTodoState.observe(this, Observer { state ->
            when (state) {
                is UnarchiveTodoState.Success -> if (state.displaySnack) displayActionSnack(R.string.unarchive_success, R.string.undo, state.todoName) { archiveTodo(false) }
                is UnarchiveTodoState.UnknownError -> displayActionSnack(R.string.error_message, R.string.retry) { unarchiveTodo(true) }
            }
        })
    }

    private fun unarchiveTodo(displaySnackOnSuccess: Boolean) {
        viewModel.unarchiveTodo(displaySnackOnSuccess)
    }

    //endregion

    //region menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        if (viewModel.shouldShowUnarchiveMenu()) {
            addMenu(menu, MENU_UNARCHIVE, R.string.action_unarchive_todo, R.drawable.ic_baseline_unarchive_white_24px)
        } else if(viewModel.shouldShowArchiveMenu()) {
            addMenu(menu, MENU_ARCHIVE, R.string.action_archive_todo, R.drawable.ic_baseline_archive_white_24px)
        }
        addMenu(menu, MENU_DELETE, R.string.action_delete_todo, R.drawable.ic_baseline_delete_white_24px)
        addMenu(menu, MENU_EDIT, R.string.action_edit_todo, R.drawable.ic_baseline_done_white_24px)
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
                displayAlertDialog(R.string.confirm_delete_message, R.string.confirm_delete_action, viewModel.todo.value!!.name) { deleteTodo() }
                true
            }
            MENU_ARCHIVE -> {
                archiveTodo(true)
                true
            }
            MENU_UNARCHIVE -> {
                unarchiveTodo(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

}
