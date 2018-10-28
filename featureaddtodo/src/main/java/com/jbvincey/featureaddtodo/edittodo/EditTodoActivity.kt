package com.jbvincey.featureaddtodo.edittodo

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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
        viewModel.todo.observe(this, Observer { todo -> addTodoEditText.setText(todo?.name) })
        addTodoEditText.validationInputEditTextListener = ValidationInputEditTextListener { saveTodo() }
    }

    //endregion

    //region edittodo

    private fun observeEditTodoState() {
        viewModel.editTodoState.observe(this, Observer { state ->
            when (state) {
                is EditTodoState.Success -> finish()
                is EditTodoState.UnknownError -> displayErrorSnack(R.string.error_message, R.string.retry) { saveTodo() }
            }
        })
    }

    private fun saveTodo() {
        viewModel.editTodo(addTodoEditText.text.toString())
    }

    //endregion

    //region deletetodo

    private fun observeDeleteTodoState() {
        viewModel.deleteTodoState.observe(this, Observer { state ->
            when (state) {
                is DeleteTodoState.Success -> finish()
                is DeleteTodoState.UnknownError -> displayErrorSnack(R.string.error_message, R.string.retry) { deleteTodo() }
            }
        })
    }

    private fun deleteTodo() {
        viewModel.deleteTodo()
    }

    //endregion

    //region menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit_todo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_edit_todo -> {
                addTodoEditText.validateText()
                true
            }
            R.id.action_delete_todo -> {
                deleteTodo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

    private fun displayErrorSnack(@StringRes messageRes: Int,
                                  @StringRes actionRes: Int,
                                  action: () -> Unit) {
        Snackbar.make(findViewById(R.id.addTodoRoot), messageRes, Snackbar.LENGTH_LONG)
                .setAction(actionRes) { action() }
                .show()
    }
}
