package com.jbvincey.todolist

import android.view.Menu
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.livedata.SingleLiveEvent
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.core.utils.add
import com.jbvincey.core.utils.exhaustive
import com.jbvincey.design.widget.helper.SwipeCallbackListener
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellCallback
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellView
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellViewModel
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by jbvincey on 19/09/2018.
 */
class TodoListArchViewModel(
    private val todoRepository: TodoRepository,
    private val todoListRepository: TodoListRepository,
    private val todoToCheckableCellViewModelTransformer: TodoToCheckableCellViewModelTransformer
) : ViewModel() {

    private val todoListId = MutableLiveData<Long>()
    private val todoListType = MutableLiveData<TodoListType>()

    private val _viewActions = SingleLiveEvent<ViewAction>()
    val viewActions: LiveData<ViewAction>
        get() = _viewActions

    val todoListName: LiveData<String> = todoListId
        .switchMap { todoListRepository.getTodoListById(it) }
        .map { it.name }

    init {
        todoToCheckableCellViewModelTransformer.checkableCellCallback = buildCheckableCellCallback()
    }

    lateinit var checkableCellViewModelList: LiveData<List<CheckableCellViewModel<Todo>>>

    fun setTodoListId(todoListId: Long) {
        this.todoListId.value = todoListId
        checkableCellViewModelList = todoListType
            .switchMap { type -> getTodoListFromType(type, todoListId) }
            .map { todos -> sortAndTransformTodoList(todos) }
    }



    private fun getTodoListFromType(type: TodoListType, todoListId: Long): LiveData<List<Todo>> =
        when (type) {
            TodoListType.UNARCHIVED -> todoRepository.getAllUnarchivedTodosFromTodoList(todoListId)
            TodoListType.ARCHIVED -> todoRepository.getAllArchivedTodosFromTodoList(todoListId)
        }.exhaustive


    private fun sortAndTransformTodoList(todoList: List<Todo>): List<CheckableCellViewModel<Todo>> {
        val sortedTodoList = todoList.toMutableList()
        sortedTodoList.sortWith(TODO_COMPARATOR)
        return todoToCheckableCellViewModelTransformer.transform(sortedTodoList)
    }

    private fun buildCheckableCellCallback() = object : CheckableCellCallback<Todo> {
        override fun onCheckChanged(data: Todo) {
            viewModelScope.launch {
                todoRepository.updateTodoCompleted(data.id)
            }
        }

        override fun onClick(data: Todo) {
            _viewActions.value = ViewAction.GoToEditTodo(data.id)
        }
    }

    fun showUnarchivedTodos() {
        todoListType.value = TodoListType.UNARCHIVED
    }

    fun showArchivedTodos() {
        todoListType.value = TodoListType.ARCHIVED
    }

    fun onBackPressed() {
        _viewActions.value = if (todoListType.value == TodoListType.ARCHIVED) {
            ViewAction.DisplayUnarchived
        } else {
            ViewAction.BackPressed
        }
    }

    fun buildSwipeStartCallback() = SwipeCallbackListener { view ->
        deleteTodo((view as CheckableCellView<Todo>).getViewModelId())
    }

    fun buildSwipeEndCallback() = SwipeCallbackListener { view ->
        archiveTodo((view as CheckableCellView<Todo>).getViewModelId())
    }

    private fun deleteTodo(todoId: Long) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todoId)
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.delete_success,
                    actionRes = R.string.undo,
                    formatArgs = arrayOf(getTodoName(todoId))
                ) { undeleteTodo(todoId) }
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { deleteTodo(todoId) }
            }
        }
    }

    private fun undeleteTodo(todoId: Long) {
        viewModelScope.launch {
            try {
                todoRepository.undeleteTodo(todoId)
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { undeleteTodo(todoId) }
            }
        }
    }

    private fun archiveTodo(todoId: Long) {
        viewModelScope.launch {
            try {
                todoRepository.archiveTodo(todoId)
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.archive_success,
                    actionRes = R.string.undo,
                    formatArgs = arrayOf(getTodoName(todoId))
                ) { unarchiveTodo(todoId) }
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { archiveTodo(todoId) }
            }
        }
    }

    private fun unarchiveTodo(todoId: Long) {
        viewModelScope.launch {
            try {
                todoRepository.unarchiveTodo(todoId)
            } catch (e: Exception) {
                _viewActions.value = ViewAction.ShowSnack(
                    messageRes = R.string.error_message,
                    actionRes = R.string.retry
                ) { unarchiveTodo(todoId) }
            }
        }
    }

    private fun getTodoName(todoId: Long): String {
        return checkableCellViewModelList.value!!.find { cellViewModel -> cellViewModel.id == todoId }!!.name
    }

    fun setBackgroundColor(@ColorRes backgroundColorRes: Int) {
        todoToCheckableCellViewModelTransformer.backgroundColorRes = backgroundColorRes
    }

    //region menu

    fun onCreateOptionsMenu(menu: Menu) {
        menu.clear()
        menu.add(MENU_EDIT, R.string.action_edit_todolist, R.drawable.ic_baseline_edit_24px)
    }

    fun onOptionItemsSelected(itemId: Int): Boolean = when (itemId) {
        MENU_EDIT -> {
            todoListId.value?.let {
                _viewActions.value = ViewAction.GoToEditTodoList(it)
                true
            } ?: false
        }
        android.R.id.home -> {
            _viewActions.value = ViewAction.Close
            true
        }
        else -> false
    }

    //endregion

    sealed class ViewAction {
        object Close : ViewAction()
        object DisplayUnarchived : ViewAction()
        object BackPressed : ViewAction()
        data class GoToEditTodoList(val todoListId: Long): ViewAction()
        data class GoToEditTodo(val todoId: Long) : ViewAction()
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

        // comparator sorting Todos: uncompleted todos fist and completed last, then sort by creation date
        val TODO_COMPARATOR = Comparator<Todo> { t1, t2 ->
            if (t1.completed != t2.completed) {
                if (t1.completed) 1 else -1
            } else {
                (t1.creationDate.time - t2.creationDate.time).toInt()
            }
        }
    }
}

enum class TodoListType {
    UNARCHIVED,
    ARCHIVED
}