package com.jbvincey.featureaddtodolist.edit

import android.view.Menu
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.livedata.SingleLiveEvent
import com.jbvincey.core.models.TodoList
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.core.utils.add
import com.jbvincey.design.widget.ValidationInputEditTextListener
import deezer.android.featureaddtodolist.R
import kotlinx.coroutines.launch

class EditTodoListArchViewModel(
    private val todoListRepository: TodoListRepository
) : ViewModel() {

    private val todoListId: MutableLiveData<Long> = MutableLiveData()
    private val todoList: LiveData<TodoList> = Transformations.switchMap(todoListId) { todoListId ->
        todoListRepository.getTodoListById(todoListId)
    }
    val todoListName: LiveData<String> = Transformations.map(todoList) { it.name }

    private val _viewActions = SingleLiveEvent<ViewAction>()
    val viewActions: LiveData<ViewAction>
        get() = _viewActions

    fun setTodoListId(todoId: Long) {
        this.todoListId.value = todoId
    }

    fun editTextListener() = ValidationInputEditTextListener { name -> name?.let { editListTodo(it) } }

    private fun editListTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoListRepository.editTodoList(todoName, todoListId.value!!)
                _viewActions.value = ViewAction.Close
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry,
                ) { editListTodo(todoName) }
            }
        }
    }

    private fun deleteTodo() {
        viewModelScope.launch {
            try {
                todoListRepository.deleteTodoList(todoListId.value!!)
                _viewActions.value = ViewAction.Close
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { deleteTodo() }
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
            _viewActions.value = ViewAction.ValidateText
            true
        }
        MENU_DELETE -> {
            _viewActions.value = ViewAction.DisplayAlertDialog(
                messageRes = R.string.confirm_delete_message,
                actionRes = R.string.confirm_delete_action,
                formatArgs = arrayOf(todoListName.value!!)
            ) { deleteTodo() }
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
    }
}