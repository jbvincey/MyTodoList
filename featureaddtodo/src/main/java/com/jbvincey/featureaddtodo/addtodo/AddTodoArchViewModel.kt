package com.jbvincey.featureaddtodo.addtodo

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.livedata.SingleLiveEvent
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.design.widget.ValidationInputEditTextListener
import com.jbvincey.featureaddtodo.R
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/**
 * Created by jbvincey on 24/09/2018.
 */
internal class AddTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    var todoListId by Delegates.notNull<Long>()

    private val _viewAction = SingleLiveEvent<ViewAction>()
    val viewAction: LiveData<ViewAction>
        get() = _viewAction

    fun editTextListener() = ValidationInputEditTextListener { name -> name?.let { addTodo(it) } }

    private fun addTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoRepository.addTodo(todoName, todoListId)
                _viewAction.value = ViewAction.Close
            } catch (e: Exception) {
                _viewAction.value = ViewAction.ShowSnack(
                    R.string.error_message,
                    R.string.retry,
                ) { addTodo(todoName) }
            }
        }
    }

    fun onOptionsItemSelected(itemId: Int): Boolean = when (itemId) {
        R.id.action_add_todo -> {
            _viewAction.value = ViewAction.ValidateText
            true
        }
        android.R.id.home -> {
            _viewAction.value = ViewAction.Close
            true
        }
        else -> false
    }

    internal sealed class ViewAction {
        object Close: ViewAction()
        object ValidateText: ViewAction()
        data class ShowSnack(
            @StringRes val messageRes: Int,
            @StringRes val actionRes: Int,
            val action: () -> Unit
        ): ViewAction()
    }
}


