package com.jbvincey.featureaddtodolist.edit

import android.view.Menu
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.core.utils.add
import com.jbvincey.design.widget.ValidationInputEditTextListener
import deezer.android.featureaddtodolist.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditTodoListArchViewModel(
    private val todoListRepository: TodoListRepository
) : ViewModel() {

    private val todoListIdFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
    val todoListNameFlow: StateFlow<String> = todoListIdFlow
        .filterNotNull()
        .flatMapLatest { todoListId -> todoListRepository.getTodoListById(todoListId) }
        .map { it.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private val viewActionChannel = Channel<ViewAction>(Channel.BUFFERED)
    val viewActionFlow = viewActionChannel.receiveAsFlow()

    fun setTodoListId(todoId: Long) {
        this.todoListIdFlow.value = todoId
    }

    fun editTextListener() = ValidationInputEditTextListener { name -> name?.let { editListTodo(it) } }

    private fun editListTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoListRepository.editTodoList(todoName, todoListIdFlow.value!!)
                viewActionChannel.send(ViewAction.Close)
            } catch (e: Exception) {
                viewActionChannel.send(ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry,
                ) { editListTodo(todoName) }
                )
            }
        }
    }

    private fun deleteTodo() {
        viewModelScope.launch {
            try {
                todoListRepository.deleteTodoList(todoListIdFlow.value!!)
                viewActionChannel.send(ViewAction.Close)
            } catch (e: Exception) {
                viewActionChannel.send(ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { deleteTodo() }
                )
            }
        }
    }

    //region menu

    fun onCreateOptionsMenu(menu: Menu) {
        menu.clear()
        menu.add(MENU_DELETE, R.string.action_delete_todolist, R.drawable.ic_baseline_delete_24px)
        menu.add(MENU_EDIT, R.string.action_edit_todolist, R.drawable.ic_baseline_done_24px)
    }

    fun onOptionItemsSelected(itemId: Int): Boolean = when(itemId) {
        MENU_EDIT -> {
            viewModelScope.launch { viewActionChannel.send(ViewAction.ValidateText) }
            true
        }
        MENU_DELETE -> {
            viewModelScope.launch { viewActionChannel.send(ViewAction.DisplayAlertDialog(
                messageRes = R.string.confirm_delete_message,
                actionRes = R.string.confirm_delete_action,
                formatArgs = arrayOf(todoListNameFlow.value!!)
            ) { deleteTodo() }
            ) }
            true
        }
        android.R.id.home -> {
            viewModelScope.launch { viewActionChannel.send(ViewAction.Close) }
            true
        }
        else -> false
    }

    //endregion

    sealed class ViewAction {
        object Close: ViewAction()
        object ValidateText: ViewAction()
        data class DisplayAlertDialog(
            @StringRes val messageRes: Int,
            @StringRes val actionRes: Int,
            val formatArgs: Array<String> = emptyArray(),
            val action: () -> Unit
        ): ViewAction() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as DisplayAlertDialog

                if (messageRes != other.messageRes) return false
                if (actionRes != other.actionRes) return false
                if (!formatArgs.contentEquals(other.formatArgs)) return false
                if (action != other.action) return false

                return true
            }

            override fun hashCode(): Int {
                var result = messageRes
                result = 31 * result + actionRes
                result = 31 * result + formatArgs.contentHashCode()
                result = 31 * result + action.hashCode()
                return result
            }
        }

        data class ShowSnack(
            @StringRes val messageRes: Int,
            @StringRes val actionRes: Int,
            val formatArgs: Array<String> = emptyArray(),
            val action: () -> Unit
        ): ViewAction() {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ShowSnack

                if (messageRes != other.messageRes) return false
                if (actionRes != other.actionRes) return false
                if (!formatArgs.contentEquals(other.formatArgs)) return false
                if (action != other.action) return false

                return true
            }

            override fun hashCode(): Int {
                var result = messageRes
                result = 31 * result + actionRes
                result = 31 * result + formatArgs.contentHashCode()
                result = 31 * result + action.hashCode()
                return result
            }
        }
    }

    companion object {
        private const val MENU_EDIT = Menu.FIRST
        private const val MENU_DELETE = MENU_EDIT + 1
    }
}