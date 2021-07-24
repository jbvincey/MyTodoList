package com.jbvincey.featureaddtodo.edittodo

import android.view.Menu
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.core.utils.add
import com.jbvincey.design.widget.ValidationInputEditTextListener
import com.jbvincey.featureaddtodo.R
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

/**
 * Created by jbvincey on 27/10/2018.
 */
internal class EditTodoArchViewModel(private val todoRepository: TodoRepository) : ViewModel() {

    private val todoIdFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val todoFlow: StateFlow<Todo?> = todoIdFlow
        .filterNotNull()
        .flatMapLatest { todoId -> todoRepository.getTodoById(todoId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val todoNameFlow: StateFlow<String> = todoFlow
        .filterNotNull()
        .map { it.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val todoArchivedFlow: StateFlow<Boolean> = todoFlow
        .filterNotNull()
        .map { it.archived }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val viewActionChannel = Channel<ViewAction>(Channel.BUFFERED)
    val viewActionFlow = viewActionChannel.receiveAsFlow()

    fun setTodoId(todoId: Long) {
        this.todoIdFlow.value = todoId
    }

    fun editTextListener() = ValidationInputEditTextListener { name -> name?.let { editTodo(it) } }

    private fun editTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoRepository.editTodo(todoName, todoIdFlow.value!!)
                viewActionChannel.send(ViewAction.Close)
            } catch (e: Exception) {
                viewActionChannel.send(ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry,
                ) { editTodo(todoName) }
                )
            }
        }
    }

    private fun deleteTodo() {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todoIdFlow.value!!)
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

    private fun archiveTodo(displaySnackOnSuccess: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.archiveTodo(todoIdFlow.value!!)
                if (displaySnackOnSuccess) {
                    viewActionChannel.send(ViewAction.ShowSnack(
                        messageRes = R.string.archive_success,
                        actionRes = R.string.cancel,
                        formatArgs = arrayOf(todoNameFlow.value),
                    ) { unarchiveTodo(displaySnackOnSuccess = false) }
                    )
                }
            } catch (e: Exception) {
                viewActionChannel.send(ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { archiveTodo(true) }
                )
            }
        }
    }

    private fun unarchiveTodo(displaySnackOnSuccess: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.unarchiveTodo(todoIdFlow.value!!)
                if (displaySnackOnSuccess) {
                    viewActionChannel.send(ViewAction.ShowSnack(
                        messageRes = R.string.unarchive_success,
                        actionRes = R.string.cancel,
                        formatArgs = arrayOf(todoNameFlow.value),
                    ) { archiveTodo(displaySnackOnSuccess = false) }
                    )
                }
            } catch (e: Exception) {
                viewActionChannel.send(ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { unarchiveTodo(true) }
                )
            }
        }
    }

    //region menu

    fun onCreateOptionsMenu(menu: Menu) {
        menu.clear()
        if (shouldShowUnarchiveMenu()) {
            menu.add(MENU_UNARCHIVE,
                R.string.action_unarchive_todo,
                R.drawable.ic_baseline_unarchive_24px)
        } else if (shouldShowArchiveMenu()) {
            menu.add(MENU_ARCHIVE,
                R.string.action_archive_todo,
                R.drawable.ic_baseline_archive_24px)
        }
        menu.add(MENU_DELETE, R.string.action_delete_todo, R.drawable.ic_baseline_delete_24px)
        menu.add(MENU_EDIT, R.string.action_edit_todo, R.drawable.ic_baseline_done_24px)
    }

    private fun shouldShowArchiveMenu(): Boolean {
        val todo = todoFlow.value
        return todo?.completed == true && !todo.archived
    }

    private fun shouldShowUnarchiveMenu(): Boolean {
        val todo = todoFlow.value
        return todo?.completed == true && todo.archived
    }

    fun onOptionItemsSelected(itemId: Int): Boolean = when (itemId) {
        MENU_EDIT -> {
            viewModelScope.launch { viewActionChannel.send(ViewAction.ValidateText) }
            true
        }
        MENU_DELETE -> {
            viewModelScope.launch {
                viewActionChannel.send(ViewAction.DisplayAlertDialog(
                    messageRes = R.string.confirm_delete_message,
                    actionRes = R.string.confirm_delete_action,
                    formatArgs = arrayOf(todoNameFlow.value)
                ) { deleteTodo() }
                )
            }
            true
        }
        MENU_ARCHIVE -> {
            archiveTodo(true)
            true
        }
        MENU_UNARCHIVE -> {
            unarchiveTodo(true)
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
        object Close : ViewAction()
        object ValidateText : ViewAction()
        data class DisplayAlertDialog(
            @StringRes val messageRes: Int,
            @StringRes val actionRes: Int,
            val formatArgs: Array<String> = emptyArray(),
            val action: () -> Unit
        ) : ViewAction() {
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
        ) : ViewAction() {

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
        private const val MENU_ARCHIVE = MENU_DELETE + 1
        private const val MENU_UNARCHIVE = MENU_ARCHIVE + 1
    }
}