package com.jbvincey.featureaddtodo.addtodo

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.jbvincey.design.widget.ValidationInputEditTextListener
import com.jbvincey.featureaddtodo.R
import kotlinx.android.synthetic.main.activity_add_todo.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by jbvincey on 23/09/2018.
 */
class AddTodoActivity : AppCompatActivity() {

    private val viewModel: AddTodoArchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        initView()
    }

    //region view setup

    private fun initView() {
        initToolbar()
        initEditText()
        observeAddTodoState()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24px)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initEditText() {
        addTodoEditText.validationInputEditTextListener = ValidationInputEditTextListener { saveTodo() }
    }

    //endregion

    //region user actions

    private fun observeAddTodoState() {
        viewModel.addTodoState.observe(this, Observer { state ->
            when(state) {
                is Success -> finish()
                is UnknownError -> displayErrorSnack(R.string.error_message, R.string.retry) { saveTodo() }
            }
        })
    }

    private fun saveTodo() {
        viewModel.addTodo(addTodoEditText.text.toString())
    }

    //endregion

    //region menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_todo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add_todo -> {
                addTodoEditText.validateText()
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