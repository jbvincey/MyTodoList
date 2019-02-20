package com.jbvincey.featureaddtodo.addtodo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jbvincey.core.repositories.TodoRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by jbvincey on 24/09/2018.
 */
class AddTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    private val disposables = CompositeDisposable()
    val addTodoState = MutableLiveData<AddTodoState>()

    fun addTodo(todoName: String) {
        disposables.add(todoRepository.addTodo(todoName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { addTodoState.value = Success },
                        { addTodoState.value = UnknownError }
                ))
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}

sealed class AddTodoState
object Success: AddTodoState()
object UnknownError: AddTodoState()