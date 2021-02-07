package com.jbvincey.featureaddtodolist.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.ui.utils.activity.displayActionSnack
import deezer.android.featureaddtodolist.R
import kotlinx.android.synthetic.main.activity_add_todolist.*
import org.koin.android.viewmodel.ext.android.viewModel

class AddTodoListActivity : AppCompatActivity() {

    private val viewModel: AddTodoListArchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todolist)
        initView()
    }

    //region view setup

    private fun initView() {
        initToolbar()
        initEditText()
        observeViewActions()
    }

    private fun initToolbar() {
        setSupportActionBar(addTodoListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEditText() {
        addTodoListEditText.validationInputEditTextListener = viewModel.editTextListener()
    }

    //endregion

    //region user actions

    private fun observeViewActions() {
        viewModel.viewActions.observe(this, Observer { action ->
            when(action) {
                AddTodoListArchViewModel.ViewAction.Close -> finish()
                AddTodoListArchViewModel.ViewAction.ValidateText -> addTodoListEditText.validateText().let{}
                is AddTodoListArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
                    messageRes = action.messageRes,
                    actionRes = action.actionRes,
                    action = action.action
                )
            }.exhaustive
        })
    }

    //endregion

    //region menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_todolist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return if (viewModel.onOptionsItemSelected(item.itemId)) {
           true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    //endregion

}