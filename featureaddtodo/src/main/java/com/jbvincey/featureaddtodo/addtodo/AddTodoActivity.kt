package com.jbvincey.featureaddtodo.addtodo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.featureaddtodo.R
import com.jbvincey.navigation.AddTodoNavigationHandler
import com.jbvincey.ui.utils.activity.displayActionSnack
import kotlinx.android.synthetic.main.activity_add_todo.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by jbvincey on 23/09/2018.
 */
class AddTodoActivity : AppCompatActivity() {

    private val viewModel: AddTodoArchViewModel by viewModel()

    private val navigationHandler: AddTodoNavigationHandler by inject()

    private val backgroundColorRes: Int by lazy { navigationHandler.retrieveBackgroundColorRes(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)
        initView()
    }

    //region view setup

    private fun initView() {
        setupTodoListId()
        initToolbar()
        initEditText()
        observeViewActions()
    }

    private fun setupTodoListId() {
        viewModel.todoListId = navigationHandler.retrieveTodoListId(intent)
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

    //endregion

    //region view actions

    private fun observeViewActions() {
        viewModel.viewAction.observe(this, Observer { action ->
            when(action) {
                AddTodoArchViewModel.ViewAction.Close -> finish()
                AddTodoArchViewModel.ViewAction.ValidateText -> addTodoEditText.validateText().let{}
                is AddTodoArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
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
        menuInflater.inflate(R.menu.menu_add_todo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return if(viewModel.onOptionsItemSelected(item.itemId)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    //endregion

}