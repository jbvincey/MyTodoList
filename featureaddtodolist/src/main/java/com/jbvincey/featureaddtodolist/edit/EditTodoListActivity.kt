package com.jbvincey.featureaddtodolist.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.navigation.EditTodoListNavigationHandler
import com.jbvincey.ui.utils.activity.displayActionSnack
import com.jbvincey.ui.utils.activity.displayAlertDialog
import deezer.android.featureaddtodolist.R
import org.koin.android.ext.android.inject
import kotlinx.android.synthetic.main.activity_add_todolist.*
import org.koin.android.viewmodel.ext.android.viewModel

class EditTodoListActivity : AppCompatActivity() {

    private val viewModel: EditTodoListArchViewModel by viewModel()

    private val navigationHandler: EditTodoListNavigationHandler by inject()

    private val backgroundColorRes: Int by lazy { navigationHandler.retrieveBackgroundColorRes(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todolist)

        initView()
    }

    //region view setup

    private fun initView() {
        setupTodoListId()
        initToolbar()
        initEditText()

        observeTodoListName()
        observeViewActions()
    }

    private fun setupTodoListId() {
        viewModel.setTodoListId(navigationHandler.retrieveTodoListId(intent))
    }

    private fun initToolbar() {
        val colorInt = ContextCompat.getColor(this, backgroundColorRes)
        addTodoListToolbar.setBackgroundColor(colorInt)
        window.statusBarColor = colorInt
        setSupportActionBar(addTodoListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEditText() {
        addTodoListEditText.validationInputEditTextListener = viewModel.editTextListener()
    }

    private fun observeTodoListName() {
        viewModel.todoListName.observe(this, Observer { todoName ->
            title = todoName
            addTodoListEditText.setText(todoName)
        })
    }

    //endregion

    //region view actions

    private fun observeViewActions() {
        viewModel.viewActions.observe(this, Observer { action ->
            when (action) {
                EditTodoListArchViewModel.ViewAction.Close -> finish()
                EditTodoListArchViewModel.ViewAction.ValidateText -> addTodoListEditText.validateText()
                    .let {}
                is EditTodoListArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
                    messageRes = action.messageRes,
                    actionRes = action.actionRes,
                    formatArgs = *action.formatArgs,
                    action = action.action
                )
                is EditTodoListArchViewModel.ViewAction.DisplayAlertDialog -> displayAlertDialog(
                    messageRes = action.messageRes,
                    actionRes = action.actionRes,
                    formatArgs = *action.formatArgs,
                    action = action.action
                )
            }.exhaustive
        })
    }

    //endregion

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

    //endregion

}

private const val MENU_EDIT = Menu.FIRST
private const val MENU_DELETE = MENU_EDIT + 1