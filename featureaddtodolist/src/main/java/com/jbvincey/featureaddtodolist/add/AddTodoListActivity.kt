package com.jbvincey.featureaddtodolist.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.ui.utils.activity.displayActionSnack
import com.jbvincey.ui.utils.activity.showSoftKeyboardWithDelay
import deezer.android.featureaddtodolist.R
import deezer.android.featureaddtodolist.databinding.ActivityAddTodolistBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class AddTodoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTodolistBinding
    private val viewModel: AddTodoListArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddTodolistBinding.inflate(layoutInflater)

        navigationHandler.setupEnterTransition(
            activity = this,
            rootView = binding.addTodoListRoot
        )

        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        initView()
    }


    //region view setup

    private fun initView() {
        initToolbar()
        initEditText()
        observeViewActions()

        showSoftKeyboardWithDelay(
            view = binding.addTodoListEditText,
            delayMillis = SHOW_SOFT_KEYBOARD_DELAY
        )
    }

    private fun initToolbar() {
        setSupportActionBar(binding.addTodoListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEditText() {
        binding.addTodoListEditText.validationInputEditTextListener = viewModel.editTextListener()
    }

    //endregion

    //region user actions

    private fun observeViewActions() {
        lifecycleScope.launch {
            viewModel.viewActionFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { action ->
                    when (action) {
                        AddTodoListArchViewModel.ViewAction.Close -> finish()
                        AddTodoListArchViewModel.ViewAction.ValidateText -> binding.addTodoListEditText.validateText()
                            .let {}
                        is AddTodoListArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
                            messageRes = action.messageRes,
                            actionRes = action.actionRes,
                            action = action.action
                        )
                    }.exhaustive
                }
        }
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

private const val SHOW_SOFT_KEYBOARD_DELAY = 400L