package com.jbvincey.featureaddtodo.edittodo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.jbvincey.core.utils.exhaustive
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

    private val viewModel: EditTodoArchViewModel by viewModel()

    private val navigationHandler: EditTodoNavigationHandler by inject()

    private val backgroundColorRes: Int by lazy { navigationHandler.retrieveBackgroundColorRes(intent) }

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

        observeTodoName()
        observeTodoArchived()
        observeViewActions()
    }

    private fun setupTodoId() {
        viewModel.setTodoId(navigationHandler.retrieveTodoId(intent))
    }

    private fun initToolbar() {
        val colorInt = ContextCompat.getColor(this, backgroundColorRes)
        addTodoToolbar.setBackgroundColor(colorInt)
        window.statusBarColor = colorInt
        setSupportActionBar(addTodoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEditText() {
        addTodoEditText.validationInputEditTextListener = viewModel.editTextListener()
    }

    private fun observeTodoName() {
        viewModel.todoName.observe(this, Observer { todoName ->
            title = todoName
            addTodoEditText.setText(todoName)
        })
    }

    private fun observeTodoArchived() {
        viewModel.todoArchived.observe(this, Observer { todoArchived ->
            addTodoEditText.isEnabled = !(todoArchived ?: false)
            invalidateOptionsMenu()
        })
    }

    //endregion

    //region view actions

    private fun observeViewActions() {
        viewModel.viewActions.observe(this, Observer { action ->
            when (action) {
                EditTodoArchViewModel.ViewAction.Close -> finish()
                EditTodoArchViewModel.ViewAction.ValidateText -> addTodoEditText.validateText().let{}
                is EditTodoArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
                    messageRes = action.messageRes,
                    actionRes = action.actionRes,
                    formatArgs = *action.formatArgs,
                    action = action.action
                )
                is EditTodoArchViewModel.ViewAction.DisplayAlertDialog -> displayAlertDialog(
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
