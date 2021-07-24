package com.jbvincey.featureaddtodolist.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.navigation.EditTodoListNavigationHandler
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.ui.utils.activity.displayActionSnack
import com.jbvincey.ui.utils.activity.displayAlertDialog
import com.jbvincey.ui.utils.activity.showSoftKeyboardWithDelay
import deezer.android.featureaddtodolist.databinding.ActivityAddTodolistBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class EditTodoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTodolistBinding
    private val viewModel: EditTodoListArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()
    private val featureNavigationHandler: EditTodoListNavigationHandler by inject()
    private val backgroundColorRes: Int by lazy { featureNavigationHandler.retrieveBackgroundColorRes(intent) }

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
        setupTodoListId()
        initToolbar()
        initEditText()

        observeTodoListName()
        observeViewActions()

        showSoftKeyboardWithDelay(
            view = binding.addTodoListEditText,
            delayMillis = SHOW_SOFT_KEYBOARD_DELAY
        )
    }

    private fun setupTodoListId() {
        viewModel.setTodoListId(featureNavigationHandler.retrieveTodoListId(intent))
    }

    private fun initToolbar() {
        val colorInt = ContextCompat.getColor(this, backgroundColorRes)
        binding.addTodoListToolbar.setBackgroundColor(colorInt)
        window.statusBarColor = colorInt
        setSupportActionBar(binding.addTodoListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEditText() {
        binding.addTodoListEditText.validationInputEditTextListener = viewModel.editTextListener()
    }

    private fun observeTodoListName() {
        lifecycleScope.launch {
            viewModel.todoListNameFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { todoName ->
                    title = todoName
                    binding.addTodoListEditText.setText(todoName)
                }
        }
    }

    //endregion

    //region view actions

    private fun observeViewActions() {
        lifecycleScope.launch {
            viewModel.viewActionFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { action ->
                    when (action) {
                        EditTodoListArchViewModel.ViewAction.Close -> finish()
                        EditTodoListArchViewModel.ViewAction.ValidateText -> binding.addTodoListEditText.validateText()
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
                }
        }
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

private const val SHOW_SOFT_KEYBOARD_DELAY = 400L