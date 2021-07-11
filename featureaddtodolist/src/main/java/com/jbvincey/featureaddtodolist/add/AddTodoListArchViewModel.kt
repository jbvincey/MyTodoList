package com.jbvincey.featureaddtodolist.add

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.design.widget.ValidationInputEditTextListener
import deezer.android.featureaddtodolist.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddTodoListArchViewModel(
    private val todoListRepository: TodoListRepository
): ViewModel() {

    private val viewActionChannel = Channel<ViewAction>(Channel.BUFFERED)
    val viewActionFlow = viewActionChannel.receiveAsFlow()

    fun editTextListener() = ValidationInputEditTextListener { name -> name?.let { addTodoList(it) } }

    private fun addTodoList(todoListName: String) {
        viewModelScope.launch {
            try {
                todoListRepository.addTodoList(todoListName)
                viewActionChannel.send(ViewAction.Close)
            } catch (e: Exception) {
                viewActionChannel.send(ViewAction.ShowSnack(
                    R.string.error_message,
                    R.string.retry
                ) {
                    addTodoList(todoListName)
                }
                )
            }
        }
    }

    fun onOptionsItemSelected(itemId: Int): Boolean = when(itemId) {
        R.id.action_add_todolist -> {
            viewModelScope.launch { viewActionChannel.send(ViewAction.ValidateText) }
            true
        }
        android.R.id.home -> {
            viewModelScope.launch { viewActionChannel.send(ViewAction.Close) }
            true
        }
        else -> false
    }

    sealed class ViewAction {
        object Close: ViewAction()
        object ValidateText: ViewAction()
        data class ShowSnack(
            @StringRes val messageRes: Int,
            @StringRes val actionRes: Int,
            val action: () -> Unit
        ) : ViewAction()
    }
}