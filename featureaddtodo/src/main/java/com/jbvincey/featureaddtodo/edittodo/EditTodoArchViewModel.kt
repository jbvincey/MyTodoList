package com.jbvincey.featureaddtodo.edittodo

import android.view.Menu
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.livedata.SingleLiveEvent
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.core.utils.add
import com.jbvincey.design.widget.ValidationInputEditTextListener
import com.jbvincey.featureaddtodo.R
import kotlinx.coroutines.launch

/**
 * Created by jbvincey on 27/10/2018.
 */
internal class EditTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    private val todoId: MutableLiveData<Long> = MutableLiveData()
    private val todo: LiveData<Todo> = Transformations.switchMap(todoId) { todoId ->
        todoRepository.getTodoById(todoId)
    }
    val todoName: LiveData<String> = Transformations.map(todo) { it.name }
    val todoArchived: LiveData<Boolean> = Transformations.map(todo) { it.archived }

    private val _viewActions = SingleLiveEvent<ViewAction>()
    val viewActions: LiveData<ViewAction>
        get() = _viewActions

    fun setTodoId(todoId: Long) {
        this.todoId.value = todoId
    }

    fun editTextListener() = ValidationInputEditTextListener { name -> name?.let { editTodo(it) } }

    private fun editTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoRepository.editTodo(todoName, todoId.value!!)
                _viewActions.value = ViewAction.Close
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry,
                ) { editTodo(todoName) }
            }
        }
    }

    private fun deleteTodo() {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todoId.value!!)
                _viewActions.value = ViewAction.Close
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { deleteTodo() }
            }
        }
    }

    private fun archiveTodo(displaySnackOnSuccess: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.archiveTodo(todoId.value!!)
                if (displaySnackOnSuccess) {
                    _viewActions.value = ViewAction.ShowSnack(
                        messageRes = R.string.archive_success,
                        actionRes = R.string.cancel,
                        formatArgs = arrayOf(todo.value!!.name),
                    ) { unarchiveTodo(displaySnackOnSuccess = false) }
                }
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { archiveTodo(true) }
            }
        }
    }

    private fun unarchiveTodo(displaySnackOnSuccess: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.unarchiveTodo(todoId.value!!)
                if (displaySnackOnSuccess) {
                    _viewActions.value = ViewAction.ShowSnack(
                        messageRes = R.string.unarchive_success,
                        actionRes = R.string.cancel,
                        formatArgs = arrayOf(todo.value!!.name),
                    ) { archiveTodo(displaySnackOnSuccess = false) }
                }
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { unarchiveTodo(true) }
            }
        }
    }

    //region menu

    fun onCreateOptionsMenu(menu: Menu) {
        menu.clear()
        if (shouldShowUnarchiveMenu()) {
            menu.add(MENU_UNARCHIVE, R.string.action_unarchive_todo, R.drawable.ic_baseline_unarchive_24px)
        } else if(shouldShowArchiveMenu()) {
            menu.add(MENU_ARCHIVE, R.string.action_archive_todo, R.drawable.ic_baseline_archive_24px)
        }
        menu.add(MENU_DELETE, R.string.action_delete_todo, R.drawable.ic_baseline_delete_24px)
        menu.add(MENU_EDIT, R.string.action_edit_todo, R.drawable.ic_baseline_done_24px)
    }

    private fun shouldShowArchiveMenu(): Boolean {
        val todo = todo.value
        return todo?.completed == true && !todo.archived
    }

    private fun shouldShowUnarchiveMenu(): Boolean {
        val todo = todo.value
        return todo?.completed == true && todo.archived
    }

    fun onOptionItemsSelected(itemId: Int): Boolean = when(itemId) {
        MENU_EDIT -> {
            _viewActions.value = ViewAction.ValidateText
            true
        }
        MENU_DELETE -> {
            _viewActions.value = ViewAction.DisplayAlertDialog(
                messageRes = R.string.confirm_delete_message,
                actionRes = R.string.confirm_delete_action,
                formatArgs = arrayOf(todoName.value!!)
            ) { deleteTodo() }
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
            _viewActions.value = ViewAction.Close
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
        private const val MENU_ARCHIVE = MENU_DELETE + 1
        private const val MENU_UNARCHIVE = MENU_ARCHIVE + 1
    }
}