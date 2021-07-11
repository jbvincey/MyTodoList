package com.jbvincey.featureaddtodo.addtodo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.featureaddtodo.R
import com.jbvincey.featureaddtodo.databinding.ActivityAddTodoBinding
import com.jbvincey.navigation.AddTodoNavigationHandler
import com.jbvincey.navigation.NavigationHandler
import com.jbvincey.ui.utils.activity.displayActionSnack
import com.jbvincey.ui.utils.activity.showSoftKeyboardWithDelay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by jbvincey on 23/09/2018.
 */
class AddTodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTodoBinding
    private val viewModel: AddTodoArchViewModel by viewModel()
    private val navigationHandler: NavigationHandler by inject()
    private val featureNavigationHandler: AddTodoNavigationHandler by inject()
    private val backgroundColorRes: Int by lazy {
        featureNavigationHandler.retrieveBackgroundColorRes(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddTodoBinding.inflate(layoutInflater)

        navigationHandler.setupEnterTransition(
            activity = this,
            rootView = binding.root
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
        observeViewActions()

        showSoftKeyboardWithDelay(
            view = binding.addTodoEditText,
            delayMillis = SHOW_SOFT_KEYBOARD_DELAY
        )
    }

    private fun setupTodoListId() {
        viewModel.todoListId = featureNavigationHandler.retrieveTodoListId(intent)
    }

    private fun initToolbar() {
        val colorInt = ContextCompat.getColor(this, backgroundColorRes)
        binding.addTodoToolbar.setBackgroundColor(colorInt)
        window.statusBarColor = colorInt
        setSupportActionBar(binding.addTodoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEditText() {
        binding.addTodoEditText.validationInputEditTextListener = viewModel.editTextListener()
    }

    //endregion

    //region view actions

    private fun observeViewActions() {
        lifecycleScope.launch {
            viewModel.viewActionFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { action ->
                    when (action) {
                        AddTodoArchViewModel.ViewAction.Close -> finish()
                        AddTodoArchViewModel.ViewAction.ValidateText -> binding.addTodoEditText.validateText()
                            .let {}
                        is AddTodoArchViewModel.ViewAction.ShowSnack -> displayActionSnack(
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
        menuInflater.inflate(R.menu.menu_add_todo, menu)
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