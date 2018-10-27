package com.jbvincey.featureaddtodo

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jbvincey.core.repositories.TodoRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by jbvincey on 24/09/2018.
 */
class AddTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    val addTodoState = MutableLiveData<AddTodoState>()

    fun addTodo(todoName: String) {
        todoRepository.addTodo(todoName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { addTodoState.value = Success },
                        { addTodoState.value = UnknownError }
                )
    }

}

sealed class AddTodoState
object Success: AddTodoState()
object UnknownError: AddTodoState()