package com.jbvincey.todolistpicker

import androidx.annotation.ColorRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.jbvincey.core.livedata.SingleLiveEvent
import com.jbvincey.core.models.TodoListWithTodos
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardCallback

import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardViewModel

class TodoListPickerArchViewModel(
    todoListRepository: TodoListRepository,
    private val todoListToStickyNoteCardViewModelTransformer: TodoListToStickyNoteCardViewModelTransformer
): ViewModel() {

    private val _viewActions = SingleLiveEvent<ViewAction>()
    val viewActions: LiveData<ViewAction>
        get() = _viewActions

    init {
        todoListToStickyNoteCardViewModelTransformer.stickyNoteCallback = buildStickyNoteCallback()
    }

    private val todoLists: LiveData<List<TodoListWithTodos>> = todoListRepository.getAllTodoListsWithTodos()
    val stickyNoteCardViewModelList: LiveData<List<StickyNoteCardViewModel<TodoListWithTodos>>> =
        todoLists.map { todoListToStickyNoteCardViewModelTransformer.transform(it) }


    private fun buildStickyNoteCallback() = object: StickyNoteCardCallback<TodoListWithTodos>{

        override fun onClick(data: StickyNoteCardViewModel<TodoListWithTodos>) {
            _viewActions.value = ViewAction.OpenTodoList(
                todoListId = data.data.todoList.id,
                todoListColorRes = data.backgroundColorRes
            )
        }

        override fun onLongClick(data: StickyNoteCardViewModel<TodoListWithTodos>): Boolean {
            _viewActions.value = ViewAction.OpenEditTodoList(
                todoListId = data.data.todoList.id,
                todoListColorRes = data.backgroundColorRes
            )
            return true
        }
    }

    sealed class ViewAction {
        data class OpenTodoList(val todoListId: Long, @ColorRes val todoListColorRes: Int): ViewAction()
        data class OpenEditTodoList(val todoListId: Long, @ColorRes val todoListColorRes: Int): ViewAction()
    }
}