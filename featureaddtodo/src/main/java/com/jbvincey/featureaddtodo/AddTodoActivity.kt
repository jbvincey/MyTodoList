package com.jbvincey.featureaddtodo

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
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
        setSupportActionBar(toolbar)

        initEditText()
        observeAddTodoState()
    }

    //region view setup

    private fun initEditText() {
        addTodoEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    saveTodo()
                    true
                }
                else -> false
            }
        }
    }

    //endregion

    //region addtodo

    private fun observeAddTodoState() {
        viewModel.addTodoState.observe(this, Observer { state ->

            when(state) {

                AddTodoArchViewModel.AddTodoState.SUCCESS -> finish()

                AddTodoArchViewModel.AddTodoState.ERROR -> displayAddTodoErrorSnack()
            }
        })
    }

    private fun displayAddTodoErrorSnack() {
        Snackbar.make(findViewById(R.id.addTodoRoot), R.string.add_todo_error, Snackbar.LENGTH_LONG)
                .setAction(R.string.add_todo_retry) { saveTodo() }
                .show()
    }

    private fun saveTodo() {
        //todo use validator
        viewModel.addTodo(addTodoEditText.text.toString())
        finish()
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
                saveTodo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

}