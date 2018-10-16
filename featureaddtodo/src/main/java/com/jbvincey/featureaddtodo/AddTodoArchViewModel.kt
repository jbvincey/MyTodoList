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
                .subscribe({
                    addTodoState.value = AddTodoState.SUCCESS
                }, {
                    addTodoState.value = AddTodoState.ERROR
                })
    }

    enum class AddTodoState {
        SUCCESS,
        ERROR
    }

}