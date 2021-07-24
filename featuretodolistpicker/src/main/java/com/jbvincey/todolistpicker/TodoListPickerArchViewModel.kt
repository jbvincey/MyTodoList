package com.jbvincey.todolistpicker

import android.view.View
import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.models.TodoListWithTodos
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardCallback

import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoListPickerArchViewModel(
    todoListRepository: TodoListRepository,
    private val todoListToStickyNoteCardViewModelTransformer: TodoListToStickyNoteCardViewModelTransformer
) : ViewModel() {

    private val viewActionChannel = Channel<ViewAction>(Channel.BUFFERED)
    val viewActionFlow = viewActionChannel.receiveAsFlow()

    val stickyNoteCardViewModelListFlow: StateFlow<List<StickyNoteCardViewModel<TodoListWithTodos>>> =
        todoListRepository.getAllTodoListsWithTodos()
            .map { todoListToStickyNoteCardViewModelTransformer.transform(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        todoListToStickyNoteCardViewModelTransformer.stickyNoteCallback = buildStickyNoteCallback()
    }

    private fun buildStickyNoteCallback() = object : StickyNoteCardCallback<TodoListWithTodos> {

        override fun onClick(data: StickyNoteCardViewModel<TodoListWithTodos>, view: View) {
            viewModelScope.launch {
                viewActionChannel.send(ViewAction.OpenTodoList(
                    todoListId = data.data.todoList.id,
                    todoListColorRes = data.backgroundColorRes,
                    view = view
                ))
            }
        }

        override fun onLongClick(data: StickyNoteCardViewModel<TodoListWithTodos>,
                                 view: View
        ): Boolean {
            viewModelScope.launch {
                viewActionChannel.send(ViewAction.OpenEditTodoList(
                    todoListId = data.data.todoList.id,
                    todoListColorRes = data.backgroundColorRes,
                    view = view
                ))
            }
            return true
        }
    }

    sealed class ViewAction {
        data class OpenTodoList(
            val todoListId: Long,
            @ColorRes val todoListColorRes: Int,
            val view: View
        ) : ViewAction()

        data class OpenEditTodoList(
            val todoListId: Long,
            @ColorRes val todoListColorRes: Int,
            val view: View
        ) : ViewAction()
    }
}